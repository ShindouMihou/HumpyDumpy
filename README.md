## 🧋 What is Humpy Dumpy?

Humpy Dumpy is a simple Discord bot that aims to provide a snapshot of the join logs given a range of time for servers. It was created as a little boredom-reliever 
based on an idea that came from suggestions of [Beemo](https://beemo.gg)'s Discord server which people wanted a simple and easy way to dump join logs.

## 💭 How does Humpy Dumpy work?

Humpy Dumpy works with the combination of MongoDB and Javacord to receive and store a snapshot of the user's information and related information onto a time-series 
collection to allow massive datasets with very little storage usage and performance impact *(200k users was around 900 kilobytes of storage space according to MongoDB Compass)*.

Humpy Dumpy uses Javalin as its web framework to enable server moderators to receive a snapshot of the server's join logs at a given time range. To learn more about how to 
query with the HTTP API, please read the section below.

## ❓ Humpy Dumpy Flow

Humpy Dumpy provides a privacy-safe and simple HTTP API for server moderators to request their server join logs within a given timestamp but before we delve into how each query 
works, let us first understand the different routes available.
- `/range.json`: This route enables for a full JSON representation of the server logs, this contains all the information needed like user name, discriminator, join timestamp, etc.
- `/range.txt`: This route is a more simpler version of the JSON route and returns a plain-text content that contains **only the users snowflakes** (intended for mass banning).

Now that we have understood what the different routes means, let us delve into the query parameters available for each one.

### 📘 JSON Route

| Parameter | Type                                                | Optional | Example                                                                                                    | Additional Notes                                                                             |
|-----------|-----------------------------------------------------|----------|------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| before    | modified rfc 1132 datetime or minutes ago since now |     ✔️    | 17 Aug 2022, 16:34:12 UTC or 30                                                                            | If using minutes ago since now, it takes the current time and minus it by the given minutes. |
| after     | modified rfc 1132 datetime or minutes ago since now |     ✔️    | 17 Aug 2022, 16:34:12 UTC or 30                                                                            | If using minutes ago since now, it takes the current time and minus it by the given minutes. |
| token     | json web token                                      |     ❌    | [View Image](https://media.discordapp.net/attachments/1001918777342042246/1009166388331560991/unknown.png) | Acquirable by using the /token command in Discord.                                           |
| onlyIds   | boolean                                             |     ✔️    | true or false                                                                                              | Makes the result return only the snowflakes of the users in a JSON format.                   |

### 📖 Plain-text Route

| Parameter | Type                                                | Optional | Example                                                                                                    | Additional Notes                                                                             |
|-----------|-----------------------------------------------------|----------|------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| before    | modified rfc 1132 datetime or minutes ago since now |     ✔️    | 17 Aug 2022, 16:34:12 UTC or 30                                                                            | If using minutes ago since now, it takes the current time and minus it by the given minutes. |
| after     | modified rfc 1132 datetime or minutes ago since now |     ✔️    | 17 Aug 2022, 16:34:12 UTC or 30                                                                            | If using minutes ago since now, it takes the current time and minus it by the given minutes. |
| token     | json web token                                      |     ❌    | [View Image](https://media.discordapp.net/attachments/1001918777342042246/1009166388331560991/unknown.png) | Acquirable by using the /token command in Discord.                                           |
| delimiter | string                                              |     ✔️    | ,                                                                                                          | Separates each user snowflake with the delimiter, this is by default a next line.            |

### 💭 Modified RFC 1132 Date Time

To make it more human-friendly to read, all routes uses a Modified RFC 1132 (17 Aug 2022, 16:34:12 UTC) variant for its parameters. It's the same as the 
RFC 1132 Date Time (Wed, 17 Aug 2022, 16:34:12 UTC) but without the week of the day (Wed) which can be a mess to modify and cause issues for the end-user.

### 💭 Minutes Ago Since Now

Minutes ago since now should be self-explanatory, it is the minutes ago since now. To understand this, let us say that our current time is 06:30 and we set a 
minutes ago since now to 30 minutes, our new time would be 06:00 because the minutes ago since now would reduce the current time by the given value (30 minutes).

### 💭 JSON Web Token

JSON Web Token is an authentication or security identifier that **should not be disclosed easily to others**. All tokens generated by Humpy Dumpy have a maximum time limit of 
one (1) hour to prevent a misuse of the authentication token. Please keep this token as safe as possible unless you want your join logs to be viewed by other people.

### 💭 Acquiring a JSON Web Token

Acquiring a JSON Web Token is as simple as running the `/token` slash command on the server that you want to use the token on. A token is limited to only one server and you cannot use it to 
view other servers that you own or manage. A token have a maximum of one hour expiration and configureable down to five minutes to ensure that it is used to expose view logs (invading privacy) 
of servers.

### 💭 Example Requests

> **Warning**
>
> Humpy Dumpy is not available publicly at the moment, but you can self-host it with the Dockerfile provided.
> Please read the self-hosting section for more information.

To understand how our requests look, let us say that we want to query our server's recent 30 minutes joins, we can simply open a new browser tab and run the following:
```
http://humpydumpy.mihou.pw/range.json?server=<server_id>&after=30&token=<token>
```

The following query requests for all recent joins after (current_time - 30 minutes).

<details>
  <summary>Preview</summary>
  
  ![image](https://user-images.githubusercontent.com/69381903/184978079-6fd3cc28-6959-476b-918d-f26ed7be996a.png)

</details>

## ❓ Self-hosting

You can self-host Humpy Dumpy by following the instructions below:
1. Clone the repository.
```shell
git clone https://github.com/ShindouMihou/HumpyDumpy && cd HumpyDumpy
```
2. Create a Mongo database. You can create a free MongoDB database via [Atlas **RECOMMENDED for beginners**](http://atlas.mongodb.com/) or by self-hosting.
3. Configure the `.env` by running the following command (Linux):
```shell
cp .env.example .env && nano .env
```
4. Build the Docker image.
```shell
docker build -t humpydumpy .
```
5. Run the Docker image.
```shell
docker run --name humpydumpy --env-file .env -p 2004:2004 -d -i -t humpydumpy:latest
```

## 🔖 License
HumpyDumpy follows Apache 2.0 license which allows the following permissions:
- ✔ Commercial Use
- ✔ Modification
- ✔ Distribution
- ✔ Patent use
- ✔ Private use

The contributors and maintainers of HumpyDumpy are not to be held liability over any creations that uses HumpyDumpy. We also forbid trademark use of
the library and there is no warranty as stated by Apache 2.0 license. You can read more about the Apache 2.0 license on [GitHub](https://github.com/ShindouMihou/HumpyDumpy/blob/master/LICENSE).
