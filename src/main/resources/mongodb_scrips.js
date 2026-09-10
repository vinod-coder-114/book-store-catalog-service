// this is a script to open the mongo shell in the docker container
docker compose exec mongo mongosh
//script to check the collections in the database
show dbs
use <database_name>
show collections