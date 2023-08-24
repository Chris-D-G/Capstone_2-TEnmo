package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.Username;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests{

    private JdbcUserDao sut;
    private static final User USER_1 = new User(1101,"kevin","password1", true);
    private static final User USER_2 = new User(1102,"chris","password2", true);
    private static final User USER_3 = new User(1103,"eric","password3", true);
    private static final User USER_4 = new User(1104,"thwin","password4", true);




    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);

    }

    @Test
    public void createNewUser() {
//        Tests sut.create AND sut.findByUsername
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }

    @Test
    public void findAllTest() {
        String username1 = USER_1.getUsername();
        String username2 = USER_2.getUsername();
        String username3 = USER_3.getUsername();
        String username4 = USER_4.getUsername();


        List<User> actualResult = sut.findAll();
        String actualname1 = actualResult.get(0).getUsername();
        String actualname2 = actualResult.get(1).getUsername();
        String actualname3 = actualResult.get(2).getUsername();
        String actualname4 = actualResult.get(3).getUsername();

        Assert.assertEquals(username1, actualname1);
        Assert.assertEquals(username2, actualname2);
        Assert.assertEquals(username3, actualname3);
        Assert.assertEquals(username4, actualname4);
    }

    @Test
    public void findOtherUsersTest() {
        List<Username> expectedResult1 = new ArrayList<>();
        List<Username> expectedResult2 = new ArrayList<>();

        String username1 = USER_1.getUsername();
        String username2 = USER_2.getUsername();
        String username3 = USER_3.getUsername();
        String username4 = USER_4.getUsername();

        Username usernameJSON1 = new Username(username1);
        Username usernameJSON2 = new Username(username2);
        Username usernameJSON3 = new Username(username3);
        Username usernameJSON4 = new Username(username4);

        expectedResult1.add(usernameJSON1);
        expectedResult1.add(usernameJSON2);
        expectedResult1.add(usernameJSON3);

        expectedResult2.add(usernameJSON2);
        expectedResult2.add(usernameJSON3);
        expectedResult2.add(usernameJSON4);


        List<Username> actualResult1 = sut.findOtherUsers(username4);
        List<Username> actualResult2 = sut.findOtherUsers(username1);

        Assert.assertEquals(expectedResult1, actualResult1);
        Assert.assertEquals(expectedResult2, actualResult2);

    }






}
