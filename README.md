# Example application using play 2.5 and reactive mongo

# Configure MongoDB

Just change it in application.conf
```
mongodb.uri = "mongodb://localhost/persons"
```

# Run it
```
sbt run
```
# Persons

Persons controller uses a model, showing how simple it is in Play to parse json data from the clients

## Add some persons

```
curl -X "POST" http://localhost:9000/persons?name\=dave\&age\=31
```

## Get person

```
curl http://localhost:9000/persons/dave
```

