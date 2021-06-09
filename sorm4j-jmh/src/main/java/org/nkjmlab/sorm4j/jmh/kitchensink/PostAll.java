package org.nkjmlab.sorm4j.jmh.kitchensink;

public class PostAll {
  public static final String DROP_AND_CREATE_TABLE =
      "DROP TABLE IF EXISTS post; create table if not exists post "
          + "(int_col int, boolean_col boolean, tinyint_col tinyint, smallint_col smallint, bigint_col bigint, decimal_col decimal, "
          + "double_col double, real_col real, time_col time, date_col date, timestamp_col timestamp, "
          + "binary_col binary, blob_col blob,  uuid_col uuid, varchar_col varchar, varchar_ignorecase_col varchar_ignorecase, "
          + "char_col char, clob_col clob, id int auto_increment primary key )";

  public static PostAll createRandom(int idx) {
    PostAll a = new PostAll();
    byte[] binaryCol = new byte[255];
    for (int i = 0; i < 255; i++)
      binaryCol[i] = (byte) (i / 2);
    a.setBinaryCol(binaryCol);
    a.setBlobCol(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
    a.setDateCol(new java.sql.Date(System.currentTimeMillis()));
    a.setTimeCol(new java.sql.Time(System.currentTimeMillis()));
    a.setTimestampCol(new java.sql.Timestamp(System.currentTimeMillis()));
    a.setCharCol("hello world char");
    a.setVarcharCol("hello world varchar");
    a.setVarcharIgnorecaseCol("hello world varchar ignore case");
    a.setClobCol("hello world clob");
    a.setIntCol(12345678);
    a.setBooleanCol(true);
    a.setTinyintCol(123);
    a.setSmallintCol(123);
    a.setBigintCol(Long.MAX_VALUE / 2);
    a.setDecimalCol(12345678);
    a.setDoubleCol(1234.5678);
    a.setRealCol(1234.56f);
    return a;
  }

  private long intCol;
  private boolean booleanCol;
  private int tinyintCol;
  private int smallintCol;
  private long bigintCol;
  private long decimalCol;
  private double doubleCol;
  private float realCol;

  public long getIntCol() {
    return intCol;
  }

  public void setIntCol(long intCol) {
    this.intCol = intCol;
  }

  public boolean getBooleanCol() {
    return booleanCol;
  }

  public void setBooleanCol(boolean booleanCol) {
    this.booleanCol = booleanCol;
  }

  public int getTinyintCol() {
    return tinyintCol;
  }

  public void setTinyintCol(int tinyintCol) {
    this.tinyintCol = tinyintCol;
  }

  public int getSmallintCol() {
    return smallintCol;
  }

  public void setSmallintCol(int smallintCol) {
    this.smallintCol = smallintCol;
  }

  public long getBigintCol() {
    return bigintCol;
  }

  public void setBigintCol(long bigintCol) {
    this.bigintCol = bigintCol;
  }

  public long getDecimalCol() {
    return decimalCol;
  }

  public void setDecimalCol(long decimalCol) {
    this.decimalCol = decimalCol;
  }

  public double getDoubleCol() {
    return doubleCol;
  }

  public void setDoubleCol(double doubleCol) {
    this.doubleCol = doubleCol;
  }

  public float getRealCol() {
    return realCol;
  }

  public void setRealCol(float realCol) {
    this.realCol = realCol;
  }

  private java.sql.Time timeCol;
  private java.sql.Date dateCol;
  private java.sql.Timestamp timestampCol;

  public java.sql.Time getTimeCol() {
    return timeCol;
  }

  public void setTimeCol(java.sql.Time timeCol) {
    this.timeCol = timeCol;
  }

  public java.sql.Date getDateCol() {
    return dateCol;
  }

  public void setDateCol(java.sql.Date dateCol) {
    this.dateCol = dateCol;
  }

  public java.sql.Timestamp getTimestampCol() {
    return timestampCol;
  }

  public void setTimestampCol(java.sql.Timestamp timestampCol) {
    this.timestampCol = timestampCol;
  }

  private byte[] binaryCol;
  private byte[] blobCol;
  private byte[] uuidCol;

  public byte[] getBinaryCol() {
    return binaryCol;
  }

  public void setBinaryCol(byte[] binaryCol) {
    this.binaryCol = binaryCol;
  }

  public byte[] getBlobCol() {
    return blobCol;
  }

  public void setBlobCol(byte[] blobCol) {
    this.blobCol = blobCol;
  }


  public byte[] getUuidCol() {
    return uuidCol;
  }

  public void setUuidCol(byte[] uuidCol) {
    this.uuidCol = uuidCol;
  }

  private String varcharCol;
  private String varcharIgnorecaseCol;
  private String charCol;
  private String clobCol;

  public String getVarcharCol() {
    return varcharCol;
  }

  public void setVarcharCol(String varcharCol) {
    this.varcharCol = varcharCol;
  }

  public String getVarcharIgnorecaseCol() {
    return varcharIgnorecaseCol;
  }

  public void setVarcharIgnorecaseCol(String varcharIgnorecaseCol) {
    this.varcharIgnorecaseCol = varcharIgnorecaseCol;
  }

  public String getCharCol() {
    return charCol;
  }

  public void setCharCol(String charCol) {
    this.charCol = charCol;
  }

  public String getClobCol() {
    return clobCol;
  }

  public void setClobCol(String clobCol) {
    this.clobCol = clobCol;
  }

  private long id;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

}
