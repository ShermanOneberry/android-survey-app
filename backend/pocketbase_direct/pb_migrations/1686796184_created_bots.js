migrate((db) => {
  const collection = new Collection({
    "id": "7zifyg3o2gz3hdo",
    "created": "2023-06-15 02:29:44.814Z",
    "updated": "2023-06-15 02:29:44.814Z",
    "name": "bots",
    "type": "auth",
    "system": false,
    "schema": [],
    "indexes": [],
    "listRule": null,
    "viewRule": null,
    "createRule": null,
    "updateRule": null,
    "deleteRule": null,
    "options": {
      "allowEmailAuth": false,
      "allowOAuth2Auth": false,
      "allowUsernameAuth": true,
      "exceptEmailDomains": [],
      "manageRule": null,
      "minPasswordLength": 8,
      "onlyEmailDomains": [],
      "requireEmail": false
    }
  });

  return Dao(db).saveCollection(collection);
}, (db) => {
  const dao = new Dao(db);
  const collection = dao.findCollectionByNameOrId("7zifyg3o2gz3hdo");

  return dao.deleteCollection(collection);
})
