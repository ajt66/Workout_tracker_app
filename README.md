# Workout Tracker App

A Simple WebApp that provides user a simple and intuitive tool to help their fitness journey.

### Currently in testing phase

---

## Features

* Allows users to create their own programs or select already available programs as well as modify and delete programs. 
* Allows users to log their daily workouts based on the active programs or extra exercises performed in the same day.
* Users can add exercises based on their muscle groups/category.
* Allows users to keep a track of their fitness and performance metrics through the analysis tab.

---

## Tech stack

* Backend
  * Java 25 - core programming language
  * [Maven](https://maven.apache.org/) - Dependency management
  * [Spring Boot](https://spring.io) - Framework
  * [Java JWT](https://github.com/jwtk/jjwt) - Library for creating and verifying JSON Web Token (JWT)
  * [PostgreSql](https://www.postgresql.org/) - Relational Database* Backend
  * [Thymeleaf](https://www.thymeleaf.org/) - server-side Java template engine
  * [HTMX](https://htmx.org/) - Open-source JavaScript library that extends HTML with custom attributes
  * [H2 Database](https://h2database.com/html/main.html) - Lightweight, in-memory relational database
  * [Chart.js](https://www.chartjs.org/) - Simple yet flexible JavaScript charting library

---

## Pre-requisites

* Java 21 or higher
* Maven 4.0.0

---

## Running the application

### Building the JAR file 

Locate the directory in the terminal and run the following command :

```sh
mvn clean package
```

Afterwards, run the following command :

```sh
java -jar target/workout-tracker-app-0.0.1-SNAPSHOT.jar
```

### Through Maven

Locate the directory in the terminal and run the following command :

```sh
mvn spring-boot:run
```

Go to the following url :

```sh
http://localhost:8080/api/home
```

Once the site is open, click the login/register button. If user chooses login page, then use the following credentials :

* username: abc1, password: abc1pass
* username: abc2 , password: abc2pass
* username: abc3, password: abc3pass

If the register button is clicked, then fill the following fields :

* username
* email
* password

Once verified the user will be directed to the main page where they can see their detais. Users can also access the website's services through the panel on the top-left panel. The following pages are currently available :

* Details: the main page.
* Programs: displays programs currently selected and available to the user.
* Workout Logs: displays the users exercise logs for a given date.
* Exercise: displays exercises available to the user.
* Analyze: displays user's performance metrics and charts.

Once the user is finished, they can use the home or the signout button on the navbar to signout of the app.

## Current Issues

* Switching newly or user added programs with pre-made programs back and forth will cause the app to bug and not display the actual active program.
