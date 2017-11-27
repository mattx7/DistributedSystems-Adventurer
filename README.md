# DistributedSystems-Adventurer

**ATTENTION: This program can only be used with the given REST-api and a HAW-account!**

## Environment

## Set up

* Enter the Jumphost via `ssh -p 443 <HAW-ID>@141.22.34.22`
* Create a ssh-key like [here](https://help.github.com/articles/checking-for-existing-ssh-keys/) and view it with `cat ~/.ssh/id_rsa.pub`
* Copy the output and insert the new key in the file `authorized_keys` in the owncloud

## Run application

* Create a new jar with the gradle commmand `fatJar` (File can be found in `/build/libs/`)
* Update the jar-file in the owncloud-directory vs_sync_2.
* Go to Container Harbor: https://141.22.34.22/ (Login with HAW-account is needed)
* Create a docker container like discribed in the  task and notice the IP for later
* Enter the Jumphost via `ssh -p 443 <HAW-ID>@141.22.34.22`
* Enter your Docker Container from the jumphost via `ssh -p 22 root@<IP-of-the-container>`
* Run the project with:  `java -jar vsp_adventurer.jar`
* Register with new User or Log in with existing one
* type !help to see available commands
