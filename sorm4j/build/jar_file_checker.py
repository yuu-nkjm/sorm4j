#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
jar_file_checker.py

Recursive JAR inspector for runtime/sources/javadoc JARs with enhanced sensitive file detection.

Reporting:
- If all JARs are OK, print a final summary for each encountered JAR type of the form:
  "enable to include:[X], excludes: [Y]"
  where X is the union of actually observed allowed patterns (per type),
  and Y is the union of forbidden categories (DOC_EXTS, CONFIG_EXTS, BINARY_EXTS, and SENSITIVE_EXTS where applicable).

Rules (filename-based where noted):
- Runtime JAR:
  Allowed: *.class, MANIFEST.MF, pom.xml, pom.properties, log4j2.xml, log4j2-*.xml.
  Severity: FORBIDDEN = document/config/sensitive extensions and sensitive name patterns. Others -> ALERT.
- Sources JAR:
  Allowed: MANIFEST.MF, pom.xml, pom.properties, log4j2*.xml, *.md, *.java.
  Severity: FORBIDDEN = binaries, document/config/sensitive extensions, and sensitive name patterns not in allowlist.
            Others -> ALERT.
- Javadoc JAR:
  Allowed: *.html, *.css, *.js, element-list,
           common static assets: *.png, *.gif, *.svg, *.jpg, *.jpeg, *.webp, *.ico,
           web fonts: *.ttf, *.woff, *.woff2,
           log4j2*.xml, *.md, MANIFEST.MF,
           LICENSE, ASSEMBLY_EXCEPTION, ADDITIONAL_LICENSE_INFO.
  Severity: FORBIDDEN = binaries, document/config/sensitive extensions, and sensitive name patterns not in allowlist.
            Others -> ALERT.
"""

from __future__ import annotations
import sys
import zipfile
from pathlib import Path
from glob import glob
from typing import Dict, Iterable, List, Optional, Set, Tuple

# --- Extension policies -------------------------------------------------------

DOC_EXTS: Set[str] = {
    ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".odt", ".ods", ".odp", ".rtf",
}

CONFIG_EXTS: Set[str] = {".yml", ".yaml", ".json", ".properties", ".xml", ".env", ".txt"}
BINARY_EXTS: Set[str] = {".class", ".jar"}

# Enhanced sensitive file extensions
SENSITIVE_EXTS: Set[str] = {
    # Cryptographic materials
    ".key", ".pem", ".p12", ".jks", ".keystore", ".crt", ".cer", ".der",
    # Database files
    ".db", ".sqlite", ".sqlite3", ".mdb", ".accdb",
    # Backup and temporary files
    ".bak", ".backup", ".old", ".orig", ".save", ".copy",
    ".tmp", ".temp", ".swp", ".swo", ".~", ".cache",
    # Log files
    ".log", ".out", ".err",
    # Archive files that might contain sensitive data
    ".zip", ".tar", ".gz", ".7z", ".rar",
    # Development/IDE files
    ".pyc", ".pyo", ".DS_Store", ".gitignore", ".git"
}

JAVADOC_STATIC_EXTS: Set[str] = {
    ".html", ".css", ".js",
    ".png", ".gif", ".svg", ".jpg", ".jpeg", ".webp", ".ico",
    ".ttf", ".woff", ".woff2",
}

# Sensitive filename patterns (case-insensitive)
SENSITIVE_PATTERNS: Set[str] = {
    "password", "passwd", "secret", "private", "credential", "cred",
    "api_key", "apikey", "token", "auth", "authentication",
    "database", "db_", "connection", "conn_", "dsn",
    "config.", ".config", "settings.", ".settings",
    "prod", "production", "staging", "dev", "development",
    "backup", "dump", "export", "migration"
}

# --- Allow helpers ------------------------------------------------------------

def is_allowed_runtime_name(name: str) -> bool:
    n = name.lower()
    return (
        n == "manifest.mf"
        or n == "pom.xml"
        or n == "pom.properties"
        or n == "log4j2.xml"
        or (n.startswith("log4j2-") and n.endswith(".xml"))
    )

def is_allowed_sources_name(name: str) -> bool:
    n = name.lower()
    return (
        n == "manifest.mf"
        or n == "pom.xml"
        or n == "pom.properties"
        or n == "log4j2.xml"
        or (n.startswith("log4j2-") and n.endswith(".xml"))
        or n.endswith(".md")
        or n.endswith(".java")
    )

def is_allowed_javadoc_name(name: str) -> bool:
    n = name.lower()
    if n in {"element-list", "manifest.mf", "pom.xml", "pom.properties", "log4j2.xml"}:
        return True
    if n in {"license", "assembly_exception", "additional_license_info"}:
        return True
    if (n.startswith("log4j2-") and n.endswith(".xml")) or n.endswith(".md"):
        return True
    return any(n.endswith(ext) for ext in JAVADOC_STATIC_EXTS)

# --- Sensitive pattern detection ----------------------------------------------

def contains_sensitive_pattern(name: str, full_path: str) -> bool:
    """Check if filename or path contains sensitive patterns."""
    lower_name = name.lower()
    lower_path = full_path.lower()
    
    # Check filename patterns
    for pattern in SENSITIVE_PATTERNS:
        if pattern in lower_name or pattern in lower_path:
            return True
    
    # Check for hidden files (starting with dot, except known good ones)
    if name.startswith('.') and name.lower() not in {'manifest.mf'}:
        return True
    
    # Check for files with multiple extensions (often suspicious)
    if name.count('.') > 2:
        return True
    
    # Check for non-standard directory structures
    path_parts = full_path.split('/')
    if len(path_parts) > 1:
        # Check for suspicious directory names
        for part in path_parts[:-1]:  # exclude filename
            if any(pattern in part.lower() for pattern in SENSITIVE_PATTERNS):
                return True
    
    return False

def has_suspicious_extension(name: str) -> bool:
    """Check if file has a suspicious extension."""
    ext = Path(name).suffix.lower()
    return ext in SENSITIVE_EXTS

# --- Label helpers ------------------------------------------------------------

def allowed_label_runtime(name: str) -> Optional[str]:
    n = name.lower()
    if n == "manifest.mf":
        return "MANIFEST.MF"
    if n == "pom.xml":
        return "pom.xml"
    if n == "pom.properties":
        return "pom.properties"
    if n == "log4j2.xml" or (n.startswith("log4j2-") and n.endswith(".xml")):
        return "log4j2*.xml"
    if n.endswith(".class"):
        return "*.class"
    return None

def allowed_label_sources(name: str) -> Optional[str]:
    n = name.lower()
    if n == "manifest.mf":
        return "MANIFEST.MF"
    if n == "pom.xml":
        return "pom.xml"
    if n == "pom.properties":
        return "pom.properties"
    if n == "log4j2.xml" or (n.startswith("log4j2-") and n.endswith(".xml")):
        return "log4j2*.xml"
    if n.endswith(".md"):
        return "*.md"
    if n.endswith(".java"):
        return "*.java"
    return None

def allowed_label_javadoc(name: str) -> Optional[str]:
    n = name.lower()
    if n == "element-list":
        return "element-list"
    if n in {"license", "assembly_exception", "additional_license_info"}:
        return n.upper() if n != "assembly_exception" else "ASSEMBLY_EXCEPTION"
    if n == "manifest.mf":
        return "MANIFEST.MF"
    if n == "pom.xml":
        return "pom.xml"
    if n == "pom.properties":
        return "pom.properties"
    if n == "log4j2.xml" or (n.startswith("log4j2-") and n.endswith(".xml")):
        return "log4j2*.xml"
    for ext in JAVADOC_STATIC_EXTS:
        if n.endswith(ext):
            return f"*{ext}"
    if n.endswith(".md"):
        return "*.md"
    return None

# --- Classification -----------------------------------------------------------

def jar_type(path: Path) -> str:
    s = path.name.lower()
    if s.endswith("-sources.jar"):
        return "sources"
    if s.endswith("-javadoc.jar"):
        return "javadoc"
    return "runtime"

def list_entries(jar_path: Path) -> List[str]:
    with zipfile.ZipFile(jar_path, "r") as jar:
        return [n for n in jar.namelist() if not n.endswith("/")]

def classify_runtime(entry: str) -> Tuple[str, str] | None:
    name = Path(entry).name
    ext = Path(entry).suffix.lower()
    
    if ext == ".class" or is_allowed_runtime_name(name):
        return None
    
    # Enhanced sensitive file detection
    if (ext in DOC_EXTS or ext in CONFIG_EXTS or 
        has_suspicious_extension(name) or 
        contains_sensitive_pattern(name, entry)):
        return (entry, "FORBIDDEN")
    
    return (entry, "ALERT")

def classify_sources(entry: str) -> Tuple[str, str] | None:
    name = Path(entry).name
    ext = Path(entry).suffix.lower()
    
    if is_allowed_sources_name(name):
        return None
    
    # Enhanced sensitive file detection
    if (ext in BINARY_EXTS or ext in DOC_EXTS or ext in CONFIG_EXTS or 
        has_suspicious_extension(name) or 
        contains_sensitive_pattern(name, entry)):
        return (entry, "FORBIDDEN")
    
    return (entry, "ALERT")

def classify_javadoc(entry: str) -> Tuple[str, str] | None:
    name = Path(entry).name
    ext = Path(entry).suffix.lower()
    
    if is_allowed_javadoc_name(name):
        return None
    
    # Enhanced sensitive file detection
    if (ext in BINARY_EXTS or ext in DOC_EXTS or ext in CONFIG_EXTS or 
        has_suspicious_extension(name) or 
        contains_sensitive_pattern(name, entry)):
        return (entry, "FORBIDDEN")
    
    return (entry, "ALERT")

def classify_entry(jar_kind: str, entry: str) -> Tuple[str, str] | None:
    if jar_kind == "runtime":
        return classify_runtime(entry)
    if jar_kind == "sources":
        return classify_sources(entry)
    return classify_javadoc(entry)

# --- Collection ---------------------------------------------------------------

def collect_labels(jar_kind: str, entries: Iterable[str]) -> Set[str]:
    labels: Set[str] = set()
    for e in entries:
        name = Path(e).name
        if jar_kind == "runtime":
            label = allowed_label_runtime(name)
        elif jar_kind == "sources":
            label = allowed_label_sources(name)
        else:
            label = allowed_label_javadoc(name)
        if label:
            labels.add(label)
    return labels

def forbidden_patterns_for(jar_kind: str) -> List[str]:
    pats: Set[str] = set()
    pats.update(f"*{ext}" for ext in DOC_EXTS)
    pats.update(f"*{ext}" for ext in CONFIG_EXTS)
    pats.update(f"*{ext}" for ext in SENSITIVE_EXTS)
    
    if jar_kind in {"sources", "javadoc"}:
        pats.add("*.class")
        pats.add("*.jar")
    
    # Add pattern-based exclusions
    pats.add("*password*")
    pats.add("*secret*")
    pats.add("*private*")
    pats.add("*credential*")
    pats.add("*api_key*")
    pats.add("*token*")
    pats.add(".*")  # hidden files
    
    return sorted(pats)

# --- Main check ---------------------------------------------------------------

def check_and_collect(jar_path: Path) -> Tuple[List[Tuple[str, str]], Set[str]]:
    entries = list_entries(jar_path)
    t = jar_type(jar_path)
    issues: List[Tuple[str, str]] = []
    for e in entries:
        r = classify_entry(t, e)
        if r is not None:
            issues.append(r)
    labels = collect_labels(t, entries)
    return issues, labels

# --- CLI ----------------------------------------------------------------------

def main(arg: str) -> int:
    path = Path(arg)
    jar_files: List[Path] = []
    if path.is_file() and path.suffix == ".jar":
        jar_files = [path]
    elif path.is_dir():
        jar_files = list(path.rglob("*.jar"))
    else:
        jar_files = [Path(p) for p in glob(arg) if p.endswith(".jar")]

    if not jar_files:
        print(f"No JAR files found for {arg}")
        return 0

    exit_code = 0
    all_ok = True
    types_seen: Set[str] = set()
    type_labels: Dict[str, Set[str]] = {"runtime": set(), "sources": set(), "javadoc": set()}

    for jf in sorted(jar_files, key=lambda p: str(p).lower()):
        t = jar_type(jf)
        types_seen.add(t)
        try:
            issues, labels = check_and_collect(jf)
        except zipfile.BadZipFile:
            print(f"❌ {jf} : not a valid JAR/ZIP")
            exit_code = 1
            all_ok = False
            continue

        type_labels[t].update(labels)

        if not issues:
            print(f"✅ {jf} : OK ({t})")
            continue
        print(f"❌ {jf} : violations found ({t})")
        for entry, level in issues:
            print(f"   [{level}] {entry}")
        exit_code = 1
        all_ok = False

    if all_ok:
        print(f"\n✅ ALL_OK\n")
        for t in ("runtime", "sources", "javadoc"):
            if t not in types_seen:
                continue
            x = ", ".join(sorted(type_labels[t])) or "(no allowed entries)"
            y = ", ".join(forbidden_patterns_for(t))
            print(f"OK: [{t}]\nenable to include: [{x}]\nexcludes: [{y}]")

    return exit_code

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python check_jar.py <DIR|JAR|GLOB>", file=sys.stderr)
        sys.exit(2)
    arg = sys.argv[1]
    sys.exit(main(arg))
