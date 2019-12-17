package org.churchsource.churchpeople.people;

import java.util.Objects;

import org.hamcrest.Description;
import org.churchsource.churchpeople.helpers.AbstractTypeSafeMatcher;

public class PersonMatcher extends AbstractTypeSafeMatcher<Person> {

  public PersonMatcher(Person expected) {
    super(expected);
  }

  @Override
  public void appendDescription(Description description, Person person) {
    description.appendText("A Person with the following state:")
        .appendText("\nId: ").appendValue(person.getId())
        .appendText("\nFirst Name: ").appendValue(person.getFirstName())
        .appendText("\nMiddle Name: ").appendValue(person.getMiddleName())
        .appendText("\nLast Name: ").appendValue(person.getLastName())
        .appendText("\nDate Of Birth: ").appendValue(person.getDateOfBirth())
        .appendText("\nDate Of Baptism: ").appendValue(person.getDateOfBaptism())
        .appendText("\nIs Deleted: ").appendValue(person.getDeleted())
        .appendText("\nGender: ").appendValue(person.getGender());
  }

  @Override
  protected boolean matchesSafely(Person actual) {
    return Objects.equals(actual.getId(), expected.getId())
        && Objects.equals(actual.getFirstName(), expected.getFirstName())
        && Objects.equals(actual.getMiddleName(), expected.getMiddleName())
        && Objects.equals(actual.getLastName(), expected.getLastName())
        && Objects.equals(actual.getDateOfBirth(), expected.getDateOfBirth())
        && Objects.equals(actual.getDateOfBaptism(), expected.getDateOfBaptism())
        && Objects.equals(actual.getDeleted(), expected.getDeleted())
        && Objects.equals(actual.getGender(), expected.getGender());
  }

  public static PersonMatcher hasSameStateAsPerson(Person expected) {
    return new PersonMatcher(expected);
  }

}
