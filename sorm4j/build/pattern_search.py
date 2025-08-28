#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Universal pattern search utility for ZIP archives and regular files.
Recursively searches for regex patterns in files with configurable filtering.
"""

from __future__ import annotations
import argparse
import glob
import re
import sys
import zipfile
from pathlib import Path
from typing import List, Tuple


def search_pattern_in_zip(zip_path: Path, pattern: re.Pattern, skip_extensions: List[str], encoding: str = 'utf-8') -> Tuple[List[Tuple[str, int, str]], dict]:
    """
    Search for regex pattern in all files within a ZIP archive.
    
    Args:
        zip_path: Path to ZIP file
        pattern: Compiled regex pattern to search for
        skip_extensions: List of file extensions to skip
        encoding: Text encoding to use when reading files
        
    Returns:
        Tuple of (matches, statistics)
        - matches: List of tuples containing (filename, line_number, matching_line)
        - statistics: Dict with processing statistics
    """
    matches: List[Tuple[str, int, str]] = []
    stats = {
        'total_files': 0,
        'skipped_files': 0,
        'processed_files': 0,
        'skipped_extensions': set(),
        'processed_extensions': set(),
        'decode_errors': 0
    }
    
    with zipfile.ZipFile(zip_path, 'r') as zip_file:
        for file_info in zip_file.infolist():
            # Skip directories
            if file_info.is_dir():
                continue
            
            stats['total_files'] += 1
            
            # Skip specified file extensions
            filename_lower = file_info.filename.lower()
            if any(filename_lower.endswith(ext.lower()) for ext in skip_extensions):
                stats['skipped_files'] += 1
                ext = Path(filename_lower).suffix
                stats['skipped_extensions'].add(ext)
                continue
                
            try:
                # Read file content
                with zip_file.open(file_info.filename) as file:
                    content = file.read()
                    
                # Try to decode as text
                try:
                    text_content = content.decode(encoding)
                except UnicodeDecodeError:
                    # Try common encodings
                    for enc in ['utf-8', 'shift_jis', 'euc-jp', 'iso-2022-jp', 'latin-1']:
                        try:
                            text_content = content.decode(enc)
                            break
                        except UnicodeDecodeError:
                            continue
                    else:
                        # Skip binary files that cannot be decoded
                        stats['decode_errors'] += 1
                        continue
                
                stats['processed_files'] += 1
                ext = Path(filename_lower).suffix or '(no extension)'
                stats['processed_extensions'].add(ext)
                
                # Search for pattern line by line
                for line_num, line in enumerate(text_content.splitlines(), 1):
                    if pattern.search(line):
                        matches.append((file_info.filename, line_num, line.strip()))
                        
            except Exception as e:
                print(f"Warning: Could not process {file_info.filename}: {e}", file=sys.stderr)
                stats['decode_errors'] += 1
                continue
    
    return matches, stats


def search_pattern_recursive(root: Path, pattern: re.Pattern, skip_extensions: List[str], encoding: str = 'utf-8') -> int:
    """
    Recursively search for regex pattern in ZIP archives and regular files under the root directory.
    
    Args:
        root: Root directory to search
        pattern: Compiled regex pattern to search for
        skip_extensions: List of file extensions to skip
        encoding: Text encoding to use when reading files
        
    Returns:
        Exit code (0 if no matches found, 1 if matches found)
    """
    zip_files = list(root.rglob("*.zip")) + list(root.rglob("*.jar"))
    
    # Also find regular files (excluding skipped extensions)
    regular_files = []
    for file_path in root.rglob("*"):
        if file_path.is_file() and file_path.suffix.lower() not in ['.zip', '.jar']:
            if not any(file_path.name.lower().endswith(ext.lower()) for ext in skip_extensions):
                regular_files.append(file_path)
    
    if not zip_files and not regular_files:
        print(f"No files found under {root}")
        return 0
    
    total_matches = 0
    total_stats = {
        'total_files': 0,
        'skipped_files': 0,
        'processed_files': 0,
        'skipped_extensions': set(),
        'processed_extensions': set(),
        'decode_errors': 0,
        'zip_files': len(zip_files),
        'regular_files': len(regular_files)
    }
    
    # Process ZIP/JAR files
    for zip_file in sorted(zip_files):
        try:
            matches, stats = search_pattern_in_zip(zip_file, pattern, skip_extensions, encoding)
            
            # Aggregate statistics
            for key in ['total_files', 'skipped_files', 'processed_files', 'decode_errors']:
                total_stats[key] += stats[key]
            total_stats['skipped_extensions'].update(stats['skipped_extensions'])
            total_stats['processed_extensions'].update(stats['processed_extensions'])
            
            if matches:
                print(f"📁 {zip_file}:")
                for filename, line_num, line in matches:
                    print(f"  {filename}:{line_num}: {line}")
                total_matches += len(matches)
            else:
                print(f"✅ {zip_file}: No matches found")
                
        except zipfile.BadZipFile:
            print(f"❌ {zip_file}: Not a valid ZIP file", file=sys.stderr)
        except Exception as e:
            print(f"❌ {zip_file}: Error processing file: {e}", file=sys.stderr)
    
    # Process regular files
    for file_path in sorted(regular_files):
        try:
            total_stats['total_files'] += 1
            
            with open(file_path, 'rb') as f:
                content = f.read()
            
            # Try to decode as text
            try:
                text_content = content.decode(encoding)
            except UnicodeDecodeError:
                # Try common encodings
                for enc in ['utf-8', 'shift_jis', 'euc-jp', 'iso-2022-jp', 'latin-1']:
                    try:
                        text_content = content.decode(enc)
                        break
                    except UnicodeDecodeError:
                        continue
                else:
                    # Skip binary files that cannot be decoded
                    total_stats['decode_errors'] += 1
                    continue
            
            total_stats['processed_files'] += 1
            ext = file_path.suffix.lower() or '(no extension)'
            total_stats['processed_extensions'].add(ext)
            
            # Search for pattern line by line
            file_matches = []
            for line_num, line in enumerate(text_content.splitlines(), 1):
                if pattern.search(line):
                    file_matches.append((line_num, line.strip()))
            
            if file_matches:
                print(f"📄 {file_path}:")
                for line_num, line in file_matches:
                    print(f"  {line_num}: {line}")
                total_matches += len(file_matches)
            else:
                print(f"✅ {file_path}: No matches found")
                
        except Exception as e:
            print(f"❌ {file_path}: Error processing file: {e}", file=sys.stderr)
            total_stats['decode_errors'] += 1
    
    # Print summary report
    if total_matches == 0:
      print("\n✅ ALL_OK: No pattern matches found in any processed files.")
    else:
      print(f"\n⚠ FOUND")
    print(f"Result: {total_matches} pattern matches found")
    print(f"\nSearch pattern: {pattern.pattern}")
    print(f"Skipped extensions: {', '.join(skip_extensions) if skip_extensions else 'None'}")
    print(f"ZIP/JAR files processed: {total_stats['zip_files']}")
    print(f"Regular files processed: {total_stats['regular_files']}")
    print(f"Total files in archives: {total_stats['total_files']}")
    print(f"Files processed for search: {total_stats['processed_files']}")
    print(f"Files skipped: {total_stats['skipped_files']}")
    print(f"Files with decode errors: {total_stats['decode_errors']}")
    
    if total_stats['skipped_extensions']:
        print(f"Actually skipped file extensions: {', '.join(sorted(total_stats['skipped_extensions']))}")
    
    if total_stats['processed_extensions']:
        print(f"Processed file extensions: {', '.join(sorted(total_stats['processed_extensions']))}")
    
    if total_matches > 0:
        return 1
    else:
        return 0


def main() -> int:
    """Main entry point."""
    parser = argparse.ArgumentParser(
        description='Universal pattern search tool for ZIP archives and regular files',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=r"""
Examples:
  # Search for Japanese characters in specific JAR file
  python pattern_search.py --pattern "[亜-熙ぁ-んァ-ヶ]" archive.jar
  
  # Search in multiple JAR files using glob pattern
  python pattern_search.py --pattern "[亜-熙ぁ-んァ-ヶ]" "../target/*.jar"
  
  # Search for error messages in directory (ZIP files and regular files)
  python pattern_search.py --pattern "error|exception" --skip ".html,.class,.js" directory/
  
  # Search for email addresses in all ZIP files in current directory
  python pattern_search.py --pattern "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}" "*.zip"
  
  # Search TODO comments in source directory with custom encoding
  python pattern_search.py --pattern "TODO|FIXME" --encoding shift_jis --skip ".bin,.exe" src/
        """
    )
    
    parser.add_argument('target', help='ZIP/JAR file, directory, or glob pattern (e.g., *.jar, ../target/*.jar)')
    parser.add_argument('--pattern', '-p', 
                       required=True,
                       help='Regular expression pattern to search for (required)')
    parser.add_argument('--skip', '-s',
                       default='',
                       help='Comma-separated list of file extensions to skip (default: none)')
    parser.add_argument('--encoding', '-e',
                       default='utf-8',
                       help='Text encoding for reading files (default: utf-8)')
    
    args = parser.parse_args()
    
    # Parse arguments
    target_pattern = args.target
    skip_extensions = [ext.strip() for ext in args.skip.split(',') if ext.strip()]
    encoding = args.encoding
    
    # Expand glob patterns
    if '*' in target_pattern or '?' in target_pattern:
        target_paths = glob.glob(target_pattern)
        if not target_paths:
            print(f"Error: No files found matching pattern '{target_pattern}'", file=sys.stderr)
            return 2
        target_paths = [Path(p) for p in target_paths]
    else:
        target_paths = [Path(target_pattern)]
    
    # Compile regex pattern
    try:
        pattern = re.compile(args.pattern)
    except re.error as e:
        print(f"Error: Invalid regex pattern '{args.pattern}': {e}", file=sys.stderr)
        return 2
    
    total_matches = 0
    total_stats = {
        'total_files': 0,
        'skipped_files': 0,
        'processed_files': 0,
        'skipped_extensions': set(),
        'processed_extensions': set(),
        'decode_errors': 0,
        'zip_files': 0,
        'regular_files': 0
    }
    
    # Process each target
    for target_path in sorted(target_paths):
        if not target_path.exists():
            print(f"Error: {target_path} does not exist", file=sys.stderr)
            continue
        
        if target_path.is_file() and target_path.suffix.lower() in ['.zip', '.jar']:
            # Single ZIP file
            try:
                matches, stats = search_pattern_in_zip(target_path, pattern, skip_extensions, encoding)
                
                # Aggregate statistics
                for key in ['total_files', 'skipped_files', 'processed_files', 'decode_errors']:
                    total_stats[key] += stats[key]
                total_stats['skipped_extensions'].update(stats['skipped_extensions'])
                total_stats['processed_extensions'].update(stats['processed_extensions'])
                total_stats['zip_files'] += 1
                
                if matches:
                    print(f"📁 {target_path}:")
                    for filename, line_num, line in matches:
                        print(f"  {filename}:{line_num}: {line}")
                    total_matches += len(matches)
                else:
                    print(f"✅ {target_path}: No matches found")
                    
            except zipfile.BadZipFile:
                print(f"❌ {target_path}: Not a valid ZIP file", file=sys.stderr)
            except Exception as e:
                print(f"❌ {target_path}: Error processing file: {e}", file=sys.stderr)
                
        elif target_path.is_dir():
            # Directory - search recursively
            result = search_pattern_recursive(target_path, pattern, skip_extensions, encoding)
            return result  # For single directory, return immediately
            
        else:
            print(f"Skipping {target_path}: Not a ZIP/JAR file or directory", file=sys.stderr)
    
    # Print summary for multiple files
    if len(target_paths) > 1 or not any(p.is_dir() for p in target_paths):
        if total_matches == 0:
          print("\n✅ ALL_OK: No pattern matches found in any processed files.")
        else:
          print(f"\n⚠ FOUND")
        print(f"Result: {total_matches} pattern matches found")
        print(f"\nSearch pattern: {pattern.pattern}")
        print(f"Skipped extensions: {', '.join(skip_extensions) if skip_extensions else 'None'}")
        print(f"ZIP/JAR files processed: {total_stats['zip_files']}")
        print(f"Regular files processed: {total_stats['regular_files']}")
        print(f"Total files in archives: {total_stats['total_files']}")
        print(f"Files processed for search: {total_stats['processed_files']}")
        print(f"Files skipped: {total_stats['skipped_files']}")
        print(f"Files with decode errors: {total_stats['decode_errors']}")
        
        if total_stats['skipped_extensions']:
            print(f"Actually skipped file extensions: {', '.join(sorted(total_stats['skipped_extensions']))}")
        
        if total_stats['processed_extensions']:
            print(f"Processed file extensions: {', '.join(sorted(total_stats['processed_extensions']))}")
        
        if total_matches > 0:
            return 1
        else:
            return 0
    
    return 0


if __name__ == "__main__":
    sys.exit(main())
