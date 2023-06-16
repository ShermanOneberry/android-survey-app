migrate((db) => {
  const collection = new Collection({
    "id": "22tej05klv78p5h",
    "created": "2023-05-12 06:52:30.635Z",
    "updated": "2023-05-12 06:52:30.635Z",
    "name": "surveyResults",
    "type": "base",
    "system": false,
    "schema": [
      {
        "system": false,
        "id": "jre5e3cz",
        "name": "userAssigned",
        "type": "relation",
        "required": true,
        "unique": false,
        "options": {
          "collectionId": "_pb_users_auth_",
          "cascadeDelete": false,
          "minSelect": null,
          "maxSelect": 1,
          "displayFields": []
        }
      },
      {
        "system": false,
        "id": "7v5eeat1",
        "name": "formData",
        "type": "json",
        "required": true,
        "unique": false,
        "options": {}
      },
      {
        "system": false,
        "id": "eafkn8cy",
        "name": "reasonImage",
        "type": "file",
        "required": true,
        "unique": false,
        "options": {
          "maxSelect": 1,
          "maxSize": 5242880,
          "mimeTypes": [],
          "thumbs": [],
          "protected": true
        }
      }
    ],
    "indexes": [],
    "listRule": null,
    "viewRule": null,
    "createRule": null,
    "updateRule": null,
    "deleteRule": null,
    "options": {}
  });

  return Dao(db).saveCollection(collection);
}, (db) => {
  const dao = new Dao(db);
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h");

  return dao.deleteCollection(collection);
})
