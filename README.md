# Roblox API (Kinda)
This program will fetch the playercounts of games you specify every 30 seconds and then save them into a database, 
from there on you are free to do whatever you want with them.

## How to use
- Download the program
- Download PostgreSQL: https://www.postgresql.org/
- Install PostgreSQL and change the port inside the program to the port your PostgreSQL server is running on:
- Example:
```java
"jdbc:postgresql://localhost:5432/"
```
- Change the following line inside the program to your servers credentials:
```java
Connection connection = prepareDatabase("postgres", "yourpassword");
```
- Make sure your PostgreSQL server is up and running, now start the program and try if it successfully connect to your server.
  If you see: "``Database connection initialized.``" appearing in the console, you're all set.
  
- Now you have to get the game-id of any Roblox game of your choice.
- Go to any Roblox games website and copy the following code inside of the URL:
<img width="269" alt="image" src="https://github.com/floriiian/RobloxAPI/assets/112857696/78cddc2d-361e-481a-9ccf-b04be01048cb">

- Now copy the following link and replace "YOURCODE" with the previously copied code:
``https://apis.roblox.com/universes/v1/places/YOURCODE/universe``

- If you did it all right you should see something like this:
```json
{
  "universeId": 3258873704
}
```
Copy the code and paste it into the program, now press enter and watch your database getting filled up.
