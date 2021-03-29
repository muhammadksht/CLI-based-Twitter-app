import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class App
{

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        String query = "SELECT Json FROM app";
        String url = "jdbc:mysql://localhost:3306/twitter?user=root";
        Connection connection = DriverManager.getConnection(url);
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);
        String json = "";
        while (rs.next())
        {
            json = rs.getString("Json");
        }
        Gson gson = new Gson();
        Twitter twitter = gson.fromJson(json, Twitter.class);
        rs.close();
        Scanner jin = new Scanner(System.in);
        String command;
        clearScreen();
        System.out.println("\t\tCLI-based Twitter application\n");
        do
        {
            command = jin.nextLine();
            if ("Sign up".equalsIgnoreCase(command))
            {
                System.out.println("Enter your name");
                String name = jin.nextLine();
                System.out.println("Enter your Email");
                String email = jin.next();
                System.out.println("Enter your password");
                String password = jin.next();
                User newUser = new User(name, email, password);
                if (twitter.signUp(newUser))
                {
                    System.out.println("Signed up");
                    Thread.sleep(2000);
                    clearScreen();
                } else
                {
                    System.err.println("Failed to sign up");
                    Thread.sleep(2000);
                    clearScreen();
                }
            }
            else if ("Login".equalsIgnoreCase(command))
            {
                System.out.println("Enter your Email");
                String email = jin.next();
                System.out.println("Enter your password");
                String password = jin.next();
                if (twitter.login(email, password))
                {
                    System.out.println("Logged in");
                    Thread.sleep(2000);
                    clearScreen();
                    do
                    {
                        command = jin.nextLine();
                        if ("My profile".equalsIgnoreCase(command))
                        {
                            twitter.myProfile();
                        }
                        else if ("Tweet".equalsIgnoreCase(command))
                        {
                            System.out.println("Type in your tweet");
                            String mainText = jin.nextLine();
                            twitter.tweet(mainText);
                            System.out.println("Tweeted successfully");
                            Thread.sleep(2000);
                            clearScreen();
                        }
                        else if ("Follow".equalsIgnoreCase(command))
                        {
                            System.out.println("Enter the name of the person you want to follow");
                            String name = jin.next();
                            if (twitter.follow(name))
                            {
                                System.out.println("Followed successfully");
                                Thread.sleep(2000);
                                clearScreen();
                            }
                            else
                            {
                                System.err.println("No such user in directory");
                                Thread.sleep(2000);
                                clearScreen();
                            }
                        }
                        else if ("Unfollow".equalsIgnoreCase(command))
                        {
                            System.out.println("Enter the name of the person you want to unfollow");
                            String name = jin.next();
                            if (twitter.unfollow(name))
                            {
                                System.out.println("Unfollowed successfully");
                                Thread.sleep(2000);
                                clearScreen();
                            }
                            else
                            {
                                System.err.println("No such user in directory or in your followed users list");
                                Thread.sleep(2000);
                                clearScreen();
                            }
                        }
                        else if ("Followers".equalsIgnoreCase(command))
                        {
                            twitter.followers();
                        }
                        else if ("Following".equalsIgnoreCase(command))
                        {
                            twitter.following();
                        }
                        else if ("TimeLine".equalsIgnoreCase(command))
                        {
                            twitter.timeline();
                        }
                        else if ("Profile".equalsIgnoreCase(command))
                        {
                            System.out.println("Enter the name of the person you want to review its tweets");
                            String name = jin.next();
                            if (twitter.profile(name))
                            {}
                            else
                            {
                                System.err.println("No such user in directory");
                                Thread.sleep(2000);
                                clearScreen();
                            }
                        }
                        else if ("Like".equalsIgnoreCase(command))
                        {
                            System.out.println("Enter the the code of the tweet which you want to like");
                            String code = jin.next();
                            if (twitter.like(code))
                            {
                                System.out.println("Liked the tweet");
                                Thread.sleep(2000);
                                clearScreen();
                            }
                            else
                            {
                                System.err.println("No tweet available with that code");
                                Thread.sleep(2000);
                                clearScreen();
                            }
                        }
                    }
                    while (!command.equalsIgnoreCase("Logout"));
                }
                else
                {
                    System.err.println("Failed to log in");
                    Thread.sleep(2000);
                    clearScreen();
                }
            }
            else if ("Help".equalsIgnoreCase(command))
            {
                help();
            }
        }
        while (!command.equalsIgnoreCase("Quit"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,true);
        objectMapper.registerModule(new JavaTimeModule());
        String jsonInString = objectMapper.writeValueAsString(twitter);
        String deleteQuery = "DELETE FROM app Where Json = '" + json + "';";
        st.executeUpdate(deleteQuery);
        String addQuery = "INSERT INTO app(Json) VALUES('" + jsonInString + "')";
        st.executeUpdate(addQuery);
    }

    public static void help() throws InterruptedException
    {
        System.out.printf
                (
                        "%-15s||\tto terminate the program\n" +
                                "%-15s||\tto make a new account\n" +
                                "%-15s||\tto register in your twitter.proj.Twitter account\n" +
                                "%-15s||\tto unregister from your twitter.proj.Twitter account\n" +
                                "%-15s||\tto get the data of the person who has logged in now\n" +
                                "%-15s||\tto make a tweet\n" +
                                "%-15s||\tto follow a user\n" +
                                "%-15s||\tto unfollow a user\n" +
                                "%-15s||\tto see a list of your followers\n" +
                                "%-15s||\tto see a list of the people you follow\n" +
                                "%-15s||\tto see a list of all tweets in details\n" +
                                "%-15s||\tto shows ones' profile\n" +
                                "%-15s||\tto like a tweet\n\n" +
                                "%-15s\n\n",
                        "Quit",
                        "Sign up",
                        "Login",
                        "Logout",
                        "My profile",
                        "Tweet",
                        "Follow",
                        "Unfollow",
                        "Followers",
                        "Following",
                        "Timeline",
                        "Profile",
                        "Like",
                        "***These commands aren't case-sensitive\n" +
                                "***names and emails has to be unique,otherwise, signing up will fail\t\n" +
                                "***any command other than the ones listed above will be ignored automatically\n" +
                                "***a valid password contains at least one digit and one special character and one uppercase letter\n" +
                                "***before logging in only \"Quit\" and \"Sign up\" and \"Help\" are available\n" +
                                "***while typing a tweet,pressing \"Enter\" means you're done with typing\n"
                );
        Thread.sleep(2000);
    }

    public static void clearScreen()
    {
        try
        {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        catch (IOException | InterruptedException ex){}
    }

}