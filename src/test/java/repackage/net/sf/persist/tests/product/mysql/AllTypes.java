
package repackage.net.sf.persist.tests.product.mysql;

public class AllTypes {

  private long id;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  // numeric
  private boolean bitCol;
  private int tinyintCol;
  private boolean booleanCol;
  private int smallintCol;
  private long mediumintCol;
  private long intCol;
  private long bigintCol;
  private float floatCol;
  private double doubleCol;
  private double decimalCol;

  public boolean getBitCol() {
    return bitCol;
  }

  public void setBitCol(boolean bitCol) {
    this.bitCol = bitCol;
  }

  public int getTinyintCol() {
    return tinyintCol;
  }

  public void setTinyintCol(int tinyintCol) {
    this.tinyintCol = tinyintCol;
  }

  public boolean getBooleanCol() {
    return booleanCol;
  }

  public void setBooleanCol(boolean booleanCol) {
    this.booleanCol = booleanCol;
  }

  public int getSmallintCol() {
    return smallintCol;
  }

  public void setSmallintCol(int smallintCol) {
    this.smallintCol = smallintCol;
  }

  public long getMediumintCol() {
    return mediumintCol;
  }

  public void setMediumintCol(long mediumintCol) {
    this.mediumintCol = mediumintCol;
  }

  public long getIntCol() {
    return intCol;
  }

  public void setIntCol(long intCol) {
    this.intCol = intCol;
  }

  public long getBigintCol() {
    return bigintCol;
  }

  public void setBigintCol(long bigintCol) {
    this.bigintCol = bigintCol;
  }

  public float getFloatCol() {
    return floatCol;
  }

  public void setFloatCol(float floatCol) {
    this.floatCol = floatCol;
  }

  public double getDoubleCol() {
    return doubleCol;
  }

  public void setDoubleCol(double doubleCol) {
    this.doubleCol = doubleCol;
  }

  public double getDecimalCol() {
    return decimalCol;
  }

  public void setDecimalCol(double decimalCol) {
    this.decimalCol = decimalCol;
  }

  // datetime
  private java.sql.Date dateCol;
  private java.sql.Timestamp datetimeCol;
  private java.sql.Timestamp timestampCol;
  private java.sql.Time timeCol;
  private short year2Col;
  private short year4Col;

  public java.sql.Date getDateCol() {
    return dateCol;
  }

  public void setDateCol(java.sql.Date dateCol) {
    this.dateCol = dateCol;
  }

  public java.sql.Timestamp getDatetimeCol() {
    return datetimeCol;
  }

  public void setDatetimeCol(java.sql.Timestamp datetimeCol) {
    this.datetimeCol = datetimeCol;
  }

  public java.sql.Timestamp getTimestampCol() {
    return timestampCol;
  }

  public void setTimestampCol(java.sql.Timestamp timestampCol) {
    this.timestampCol = timestampCol;
  }

  public java.sql.Time getTimeCol() {
    return timeCol;
  }

  public void setTimeCol(java.sql.Time timeCol) {
    this.timeCol = timeCol;
  }

  public short getYear2Col() {
    return year2Col;
  }

  public void setYear2Col(short year2Col) {
    this.year2Col = year2Col;
  }

  public short getYear4Col() {
    return year4Col;
  }

  public void setYear4Col(short year4Col) {
    this.year4Col = year4Col;
  }

  // string
  private String charCol;
  private String varcharCol;
  private String tinytextCol;
  private String textCol;
  private String mediumtextCol;
  private String longtextCol;
  private String enumCol;
  private String setCol;

  public String getCharCol() {
    return charCol;
  }

  public void setCharCol(String charCol) {
    this.charCol = charCol;
  }

  public String getVarcharCol() {
    return varcharCol;
  }

  public void setVarcharCol(String varcharCol) {
    this.varcharCol = varcharCol;
  }

  public String getTinytextCol() {
    return tinytextCol;
  }

  public void setTinytextCol(String tinytextCol) {
    this.tinytextCol = tinytextCol;
  }

  public String getTextCol() {
    return textCol;
  }

  public void setTextCol(String textCol) {
    this.textCol = textCol;
  }

  public String getMediumtextCol() {
    return mediumtextCol;
  }

  public void setMediumtextCol(String mediumtextCol) {
    this.mediumtextCol = mediumtextCol;
  }

  public String getLongtextCol() {
    return longtextCol;
  }

  public void setLongtextCol(String longtextCol) {
    this.longtextCol = longtextCol;
  }

  public String getEnumCol() {
    return enumCol;
  }

  public void setEnumCol(String enumCol) {
    this.enumCol = enumCol;
  }

  public String getSetCol() {
    return setCol;
  }

  public void setSetCol(String setCol) {
    this.setCol = setCol;
  }

  // binary

  private byte[] binaryCol;
  private byte[] varbinaryCol;
  private byte[] tinyblobCol;
  private byte[] blobCol;
  private byte[] mediumblobCol;
  private byte[] longblobCol;

  public byte[] getBinaryCol() {
    return binaryCol;
  }

  public void setBinaryCol(byte[] binaryCol) {
    this.binaryCol = binaryCol;
  }

  public byte[] getVarbinaryCol() {
    return varbinaryCol;
  }

  public void setVarbinaryCol(byte[] varbinaryCol) {
    this.varbinaryCol = varbinaryCol;
  }

  public byte[] getTinyblobCol() {
    return tinyblobCol;
  }

  public void setTinyblobCol(byte[] tinyblobCol) {
    this.tinyblobCol = tinyblobCol;
  }

  public byte[] getBlobCol() {
    return blobCol;
  }

  public void setBlobCol(byte[] blobCol) {
    this.blobCol = blobCol;
  }

  public byte[] getMediumblobCol() {
    return mediumblobCol;
  }

  public void setMediumblobCol(byte[] mediumblobCol) {
    this.mediumblobCol = mediumblobCol;
  }

  public byte[] getLongblobCol() {
    return longblobCol;
  }

  public void setLongblobCol(byte[] longblobCol) {
    this.longblobCol = longblobCol;
  }

}
