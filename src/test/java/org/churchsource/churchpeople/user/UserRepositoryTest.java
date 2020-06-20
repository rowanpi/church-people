package org.churchsource.churchpeople.user;

import org.churchsource.churchpeople.address.Address;
import org.churchsource.churchpeople.model.type.AddressType;
import org.churchsource.churchpeople.model.type.Gender;
import org.churchsource.churchpeople.user.CPUserDetails;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;

import static org.churchsource.churchpeople.address.Address.anAddress;
import static org.churchsource.churchpeople.user.helpers.RoleMatcher.hasSameStateAsRole;
import static org.churchsource.churchpeople.user.CPUserDetails.aCPUserDetails;
import static org.churchsource.churchpeople.user.helpers.CPUserDetailsMatcher.hasSameStateAsCPUserDetails;
import static org.churchsource.churchpeople.user.Privilege.aPrivilege;
import static org.churchsource.churchpeople.user.Role.aRole;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {

  @PersistenceContext
  protected EntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void testSaveCPUserDetails_shouldPersistCPUserDetails() {

    List<Role> roles = new ArrayList<Role>();
    List<Privilege> privileges = new ArrayList<Privilege>();
    privileges.add(aPrivilege().name("priv1").deleted(false).build());
    privileges.add(aPrivilege().name("priv2").deleted(false).build());
    Role aRole = aRole().name("role_1").privileges(privileges).deleted(false).build();
    roles.add(aRole);

    CPUserDetails aCPUserDetails = aCPUserDetails().username("Joe").password("Bar").isEnabled(true).deleted(false).roles(roles).build();

    CPUserDetails savedCPUserDetails = userRepository.save(aCPUserDetails);

    // check that the CPUserDetails entity was persisted correctly
    assertThat(savedCPUserDetails.getId(), is(IsNull.notNullValue()));

    CPUserDetails retrievedCPUserDetails = entityManager.createQuery("SELECT u FROM CPUserDetails u WHERE u.id = :id", CPUserDetails.class)
        .setParameter("id", savedCPUserDetails.getId()).getSingleResult();
    Collection<Role> retrievedRoles = retrievedCPUserDetails.getRoles();

    assertThat(retrievedCPUserDetails, hasSameStateAsCPUserDetails(savedCPUserDetails));
    assertThat(retrievedRoles.size(), is(1));
    assertThat((Role)retrievedRoles.toArray()[0], hasSameStateAsRole(aRole));
  }

  @Test
  public void testUpdateCPUserDetails_shouldMergeCPUserDetails() {
    List<Role> roles = new ArrayList<Role>();
    List<Privilege> privileges = new ArrayList<Privilege>();
    privileges.add(aPrivilege().name("priv1").deleted(false).build());
    privileges.add(aPrivilege().name("priv2").deleted(false).build());
    Role aRole = aRole().name("role_1").privileges(privileges).deleted(false).build();
    roles.add(aRole);

    CPUserDetails aCPUserDetails = aCPUserDetails().username("Joe").password("Bar").isEnabled(true).deleted(false).roles(roles).build();

    entityManager.persist(aCPUserDetails);
    entityManager.flush();

    CPUserDetails newUpdatedCPUserDetails = aCPUserDetails().id(aCPUserDetails.getId()).username("JoeUpdated").password("Bar").isEnabled(true).deleted(false).roles(roles).build();

    CPUserDetails updatedMergedCPUserDetails = userRepository.updateUser(newUpdatedCPUserDetails);

    // check that the CPUserDetails entity was persisted correctly
    assertThat(updatedMergedCPUserDetails.getId(), is(IsNull.notNullValue()));

    CPUserDetails retrievedCPUserDetails = entityManager.createQuery("SELECT u FROM CPUserDetails u WHERE u.id = :id", CPUserDetails.class)
        .setParameter("id", updatedMergedCPUserDetails.getId()).getSingleResult();

    assertThat(retrievedCPUserDetails, hasSameStateAsCPUserDetails(newUpdatedCPUserDetails));
  }

  @Test
  public void testUpdateCPUserDetailsWithoutAddresses_shouldMergeCPUserDetailsAndRemoveAddresses() {
    List<Role> roles = new ArrayList<Role>();
    List<Privilege> privileges = new ArrayList<Privilege>();
    privileges.add(aPrivilege().name("priv1").deleted(false).build());
    privileges.add(aPrivilege().name("priv2").deleted(false).build());
    Role aRole = aRole().name("role_1").privileges(privileges).deleted(false).build();
    roles.add(aRole);

    CPUserDetails aCPUserDetails = aCPUserDetails().username("Joe").password("Bar").isEnabled(true).deleted(false).roles(roles).build();

    entityManager.persist(aCPUserDetails);
    entityManager.flush();

    CPUserDetails newUpdatedCPUserDetails = aCPUserDetails().id(aCPUserDetails.getId()).username("JoeUpdated").password("Bar").isEnabled(true).deleted(false).roles(null).build();

    CPUserDetails updatedMergedCPUserDetails = userRepository.updateUser(newUpdatedCPUserDetails);

    // check that the CPUserDetails entity was persisted correctly
    assertThat(updatedMergedCPUserDetails.getId(), is(IsNull.notNullValue()));

    CPUserDetails retrievedCPUserDetails = entityManager.createQuery("SELECT u FROM CPUserDetails u WHERE u.id = :id", CPUserDetails.class)
        .setParameter("id", updatedMergedCPUserDetails.getId()).getSingleResult();

    assertThat(retrievedCPUserDetails, hasSameStateAsCPUserDetails(newUpdatedCPUserDetails));

    List<Role> retrievedRoles = retrievedCPUserDetails.getRoles();

    assertThat(retrievedCPUserDetails, hasSameStateAsCPUserDetails(newUpdatedCPUserDetails));
    assertThat(retrievedRoles, is((List<Role>)null));
  }

  @Test
  public void testUpdateCPUserDetailsWithNullRoles_shouldMergeCPUserDetails() {
    CPUserDetails aCPUserDetails = aCPUserDetails().username("Joe").password("Bar").isEnabled(true).deleted(false).roles(null).build();

    entityManager.persist(aCPUserDetails);
    entityManager.flush();

    CPUserDetails newUpdatedCPUserDetails = aCPUserDetails().id(aCPUserDetails.getId()).username("JoeUpdated").password("Bar").isEnabled(true).deleted(false).roles(null).build();

    CPUserDetails updatedMergedCPUserDetails = userRepository.updateUser(newUpdatedCPUserDetails);

    // check that the CPUserDetails entity was persisted correctly
    assertThat(updatedMergedCPUserDetails.getId(), is(IsNull.notNullValue()));

    CPUserDetails retrievedCPUserDetails = entityManager.createQuery("SELECT u FROM CPUserDetails u WHERE u.id = :id", CPUserDetails.class)
            .setParameter("id", updatedMergedCPUserDetails.getId()).getSingleResult();

    assertThat(retrievedCPUserDetails, hasSameStateAsCPUserDetails(newUpdatedCPUserDetails));
  }

  @Test(expected = EmptyResultDataAccessException.class)
  public void testUpdateCPUserDetailsThatDoesNotExist_shouldThrowException() {
    CPUserDetails CPUserDetails = aCPUserDetails().username("Joe").password("Bar").deleted(false).build();

    CPUserDetails newUpdatedCPUserDetails = aCPUserDetails().id(999L).username("JoeUpdated").deleted(CPUserDetails.getDeleted()).build();

    userRepository.updateUser(newUpdatedCPUserDetails);
  }

  @Test
  public void testDeleteCPUserDetails_shouldMarkCPUserDetailsAsDeleted() {
    CPUserDetails aCPUserDetails = aCPUserDetails().username("Joe").password("Bar").isEnabled(true).deleted(false).roles(null).build();


    entityManager.persist(aCPUserDetails);
    entityManager.flush();

    CPUserDetails deleted = aCPUserDetails()
            .id(aCPUserDetails.getId())
            .username(aCPUserDetails.getUsername())
            .password(aCPUserDetails.getPassword())
            .isEnabled(aCPUserDetails.isEnabled())
            .roles(aCPUserDetails.getRoles())
            .deleted(true)
            .build();

    userRepository.deleteUser(aCPUserDetails.getId());

    // TODO check that the associated entities has been deleted in a cascade event, if set to do so
    // (NONE YET PRESENT)

    CPUserDetails retrievedCPUserDetails = entityManager.createQuery("SELECT p FROM CPUserDetails p WHERE p.id = :id", CPUserDetails.class)
        .setParameter("id", deleted.getId()).getSingleResult();

    assertThat(retrievedCPUserDetails, hasSameStateAsCPUserDetails(deleted));
  }

  @Test(expected = EmptyResultDataAccessException.class)
  public void testDeleteCPUserDetailsThatDoesNotExist_shouldThrowException() {
    userRepository.deleteUser(999L);
  }

  @Test
  public void testFindCPUserDetailsByIdThatExists_shouldFindCPUserDetails() {
    CPUserDetails CPUserDetails = aCPUserDetails().username("Joe").password("Bar").deleted(false).build();

    entityManager.persist(CPUserDetails);
    entityManager.flush();

    CPUserDetails retrievedCPUserDetails = userRepository.findUserById(CPUserDetails.getId());

    assertThat(retrievedCPUserDetails, hasSameStateAsCPUserDetails(CPUserDetails));
  }

  @Test(expected = NoResultException.class)
  public void testFindCPUserDetailsByIdThatDoesNotExists_shouldThrowAnException() {
    userRepository.findUserById(999L);
  }

  @Test
  public void testFindCPUserDetailsByUserName_shouldRetreiveNonDeletedUserWithUserName() {
    CPUserDetails aUser = aCPUserDetails().username("Joe").password("Bar").deleted(false).build();
    entityManager.persist(aUser);
    entityManager.flush();

    CPUserDetails aUser2 = aCPUserDetails().username("JoeSoap").password("Bar").deleted(false).build();
    entityManager.persist(aUser2);
    entityManager.flush();

    CPUserDetails aUser3 = aCPUserDetails().username("JoeSoapDish").password("Bar").deleted(false).build();
    entityManager.persist(aUser3);
    entityManager.flush();

    CPUserDetails user = userRepository.findUserByUserName("Joe");
    assertThat(user, hasSameStateAsCPUserDetails(aUser));
  }

  @Test(expected = NoResultException.class)
  public void testFindCPUserDetailsByNameThatIsDeleted_shouldNotReturnResult() {
    CPUserDetails aUser = aCPUserDetails().username("Joe").password("Bar").deleted(true).build();
    entityManager.persist(aUser);
    entityManager.flush();

    CPUserDetails aUser2 = aCPUserDetails().username("JoeSoap").password("Bar").deleted(false).build();
    entityManager.persist(aUser2);
    entityManager.flush();

    CPUserDetails aUser3 = aCPUserDetails().username("JoeSoapDish").password("Bar").deleted(false).build();
    entityManager.persist(aUser3);
    entityManager.flush();

    CPUserDetails user = userRepository.findUserByUserName("Joe");
  }

  @Test(expected = NoResultException.class)
  public void testFindCPUserDetailsByNameThatIsNotValid_shouldNotReturnResult() {
    CPUserDetails aUser = aCPUserDetails().username("Joe").password("Bar").deleted(true).build();
    entityManager.persist(aUser);
    entityManager.flush();

    CPUserDetails user = userRepository.findUserByUserName("Knob");
  }
}