# App Backend

This is a collection of code that is meant to live in the backend.

- `pocketbase_direct` just contains a pocketbase installation.
  It's best used when we can use Let's Encrypt for Certificate signing.

  TODO: Consider replacing this with a link to the Pocketbase project

- `pocketbase_docker_windows` contains a dockerized version of pocketbase, with reverse proxy. 
  This is best used for when we cannot use Let's Encrypt. 

- `pocketbase_scripts` contains a node project with scripts for loading incoming Excel data, and generating outgoing Excel reports.

## Installation

//TODO: Add full installation here once I get all the details, 
preferably with an all in one script to install/start up

## Usage

### `pocketbase_scripts`

The background scripts `load_db` and `generate_report` are avalaible, and are intended to be run from a cron job. 

Credentials for a bot account are needed from the `bots` collection of Pocketbase, in order for the scripts to run.
Store them at `backend\pocketbase_scripts\.env`, as well as the URL of the Pocketbase backend.
See `backend\pocketbase_scripts\.env_example` for an example of how to formant the `.env` file. 

Commands:
- `npm run load_db` will take Excel files in the folder `proposed_sites`, and load them onto the Pocketbase backend. This is needed for both upload verification, and for later report generation. 
  - *NOTE* that the command is idempotent, and that you can also run the command on updated Excel files.
  - *NOTE* that the Excel files are expected to follow a very particualr format, and that any changes 
  - TODO: Get the load folder to be changable by cli args, with sensible defaults
- `npm run generate_report` will generate the desired Excel reports from the site surveys and the data obtained from the `load_db` script. Currently, the generated reports will be placed in the `generated_reports` folder.
  - TODO: Let the folder that the reports be placed in be configurable by cli args, with sensible defaults

//TODO: Add a guide here explaining the different components, and where to put in request excel files,
and where to collect files

## Contributing

//TO BE DECIDED

## License

//TO BE DECIDED