# App Backend

This is a collection of code that is meant to live in the backend.

- `pocketbase_direct` just contains a pocketbase installation.
  It's best used when we can use Let's Encrypt for Certificate signing.
  - An up-to-date version may be found at https://pocketbase.io/docs/

- `pocketbase_docker_windows` contains a dockerized version of pocketbase, with reverse proxy. 
  This is best used for when we cannot use Let's Encrypt.
  - Note that the app is not configured to accept self-signed certs, due to development constraints. 

- `pocketbase_scripts` contains a node project with scripts for loading incoming Excel data, and generating outgoing Excel reports.

## Installation

If you are deploying Pocketbase by some other means, to set up the collections, navigate to 'Settings -> Import collections', and paste the contents of `backend\collections_backup.json`.

### `pocketbase_direct`

- No particular setup is needed to start up a fresh Pocketbase backend,
  - However, user accounts under the `users` and `bots` collection will need to be set up manually though the admin console. 
  - These collections are for app users, and the scripts running `pocketbase_scripts` respectively.
- To populate a Pocketbase instance with existing data, you will need to copy the `pb_data` folder over to the `backend\pocketbase_direct` folder, relative to the project root

### `pocketbase_docker_windows`

- For a fresh backend install, the only difference from `pocketbase_direct` is that one will need to install docker first. 
- No accomidation has been made to transfer over an existing `pb_data` folder at this time. 

### `pocketbase_scripts`

- Before using pocketbase scripts, you will need to run `npm i` from the folder `backend\pocketbase_scripts` relative to the project root. 
- This is needed to install the prerequisite node modules. 

## Usage

### `pocketbase_direct`

- To run Pocketbase using this configuration, simply run the command `pocketbase serve` from the folder `backend\pocketbase_direct`, relative to the project root.
- The application data is split up into 4 'collections'
  - `users` contain all the users that will be logging into the app. Note that the `name` field is what will appear in the auto-generated reports.
    - Note that this will need to be manually added to from the admin panel.
  - `bots` will contain the credentials that should be passed onto `pocketbase_scripts` in order to both load the backed with necessary data for validation, and generate reports from the collected survey data
    - Note that this will need to be manually added to from the admin panel.
  - `surveyDetails` contains all the information about all the surveys currently in progress. They are intended to be added to using the `load_db` script in `pocketbase_scripts`
  - `surveyResults` contains all the information inputed by the frontend app. This will be used in the `generate_report` script of `pocketbase_scripts` during report generation

### `pocketbase_docker_windows`

- To run Pocketbase using this configuration, simply run the command `docker compose up` from the folder `backend\pocketbase_docker_windows`, relative to the project root.
  - Again, Note that the app is not configured to accept self-signed certs, due to development constraints. 

### `pocketbase_scripts`

The background scripts `load_db` and `generate_report` are avalaible, and are intended to be run from a cron job. 

Credentials for a bot account are needed from the `bots` collection of Pocketbase, in order for the scripts to run.
Store them at `backend\pocketbase_scripts\.env`, as well as the URL of the Pocketbase backend.
See `backend\pocketbase_scripts\.env_example` for an example of how to formant the `.env` file. 

Commands:
- `npm run load_db` will take Excel files in the folder `proposed_sites`, and load them onto the Pocketbase backend. This is needed for both upload verification, and for later report generation. 
  - *NOTE* that the command is idempotent, and that you can also run the command on updated Excel files.
  - *NOTE* that the Excel files are expected to follow a very particular format, and that any changes 
  - TODO: Get the load folder to be changable by cli args, with sensible defaults
- `npm run generate_report` will generate the desired Excel reports from the site surveys and the data obtained from the `load_db` script. Currently, the generated reports will be placed in the `generated_reports` folder.
  - TODO: Let the folder that the reports be placed in be configurable by cli args, with sensible defaults
- `npm run build` is a development only command, meant to be run only when there is a signifcant change to the Pocketbase backend. 
(E.G: New/Renamed/Deleted collection(s) or collection member(s))
  - NOTE: Maunally setting only one of the system fields but not the other (I.E: `created`, `updated`), causes the other system field to not be auto-generated. The resulting missing system field breaks type generation. 

## Contributing

//TO BE DECIDED

## License

//TO BE DECIDED