package org.nkjmlab.sorm4j.example;

import static org.nkjmlab.sorm4j.util.table.TableSchema.Keyword.*;
import java.util.List;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.util.table.TableSchema;
import org.nkjmlab.sorm4j.util.table.TableSchema.Keyword;

public class TableSchemaExample {

  public static void main(String[] args) {
    String jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;";
    String user = "username";
    String password = "password";
    DataSource dataSorce = Sorm.createDataSource(jdbcUrl, user, password);

    QuizTable quizTable = new QuizTable(dataSorce);

    quizTable.insert(new Quiz("book1", "What is this?", "apple"));

  }


  public static class QuizTable {
    private final TableSchema schema;
    private final Sorm sorm;

    public static final String TABLE_NAME = "QUIZ";

    private static final String ID = "id";
    private static final String BOOK_NAME = "book_name";
    private static final String QUESTION = "question";
    private static final String ANSWER = "answer";



    public QuizTable(DataSource dataSorce) {
      this.sorm = Sorm.create(dataSorce);
      this.schema = TableSchema.builder().setTableName(TABLE_NAME)
          .addColumnDefinition(ID, Keyword.INT, Keyword.AUTO_INCREMENT,
              Keyword.PRIMARY_KEY)
          .addColumnDefinition(BOOK_NAME, VARCHAR).addColumnDefinition(BOOK_NAME, VARCHAR)
          .addColumnDefinition(QUESTION, VARCHAR).addColumnDefinition(ANSWER, VARCHAR).build();
    }

    public void insert(Quiz quiz) {
      sorm.insert(quiz);
    }

    public void dropTableIfExists() {
      sorm.executeUpdate(schema.getDropTableIfExistsStatement());
    }

    public void createTableAndIndexesIfNotExists() {
      sorm.accept(conn -> {
        conn.executeUpdate(schema.getCreateTableIfNotExistsStatement());
        schema.getCreateIndexIfNotExistsStatements()
            .forEach(createIndexStatement -> conn.executeUpdate(createIndexStatement));
      });
    }

    public List<Quiz> readAllQuizzes() {
      return sorm.readList(Quiz.class, "select * from " + TABLE_NAME);
    }

    public List<String> readAllBookNames() {
      return sorm.readList(String.class, "select distinct " + BOOK_NAME + " from " + TABLE_NAME);
    }
  }

  public static class Quiz {

    public int id;
    public String bookName;
    public String question;
    public String answer;

    public Quiz() {}

    public Quiz(String bookName, String question, String answer) {
      this.bookName = bookName;
      this.question = question;
      this.answer = answer;
    }



  }


}
