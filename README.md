# Prestacop_NYPD_Software_Suite
POC for a NYPD Software Suite using Scala, Spark, Kafka, MongoDB and Redis (School project)

## Team members

- **Sébastien BERNARD**
- **Camille BRIAND**
- **Élodie DEHACHE**
- **Jules LAGNY**


## Required configuration

This project relies on various services and pieces of software running separately (and remotely when possible)

- **Zookeeper** on remote server, running on port 2181
- **Kafka** on remote server, running on port 9092
- **MongoDB** on remote server, running on port 27017
- **Redis** on remote server, running on port 6379
- **`jupyter/all-spark-notebook` Docker container** on remote server, running on port 8888 (notebook exposed is setup for
Spark development)
- *(Alternative option:) **Apache Zeppelin** on remote server, running on port 8080*



## Important notice

All pieces of software in this repo are connected to a server that was setup only for demonstration purposes 
and which IP was not hidden.

The project will be updated to allow the use of a configuration file for setting up your own server, URLs and 
Kafka streams.
