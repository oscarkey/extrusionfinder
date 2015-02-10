package uk.ac.cam.echo.extrusionfinder.database;

import com.mongodb.BasicDBObject;
import org.mongojack.JacksonDBCollection;

/**
 * Provides facilities for saving and loading objects of type T from a specified MongoDB
 * collection
 *
 * @author as2388
 */
class MongoDBCollectionManager<T extends DatabaseItem> {
    private final JacksonDBCollection<T, String> collection;

    /**
     * @param collection    MongoDB collection to use for saving and loading objects into
     */
    public MongoDBCollectionManager(JacksonDBCollection<T, String> collection) {
        this.collection = collection;
    }

    /**
     * Saves an item into the MongoDB collection
     * @param item  Item to save
     */
    public void save(T item) {
        if (contains(item.get_id())) {
            collection.updateById(item.get_id(), item);
        } else {
            collection.insert(item);
        }
    }

    /**
     * Checks if an item is currently in the MongoDB collection
     * @param _id   Identifier of item to look for
     * @return      True if the item is in the collection, false otherwise
     */
    public boolean contains(String _id) {
        // Count the number of matches in the collection which have identifier _id
        int numberOfMatches = collection.find(new BasicDBObject("_id", _id)).count();

        assert (numberOfMatches == 0 || numberOfMatches == 1);

        return numberOfMatches == 1;
    }

    /**
     * Loads the item with id '_id' from the MongoDB collection
     * @param _id   Identifier of item to look for
     * @return      Item with identifier _id
     * @throws ItemNotFoundException    Thrown if no item with identifier _id was found in the collections
     */
    public T load(String _id) throws ItemNotFoundException {
        if (contains(_id)) {
            // The collection only contains one object with property '_id',
            // so findByOneId here means 'find the only one'
            return collection.findOneById(_id);
        } else {
            throw new ItemNotFoundException("Item with id '" + _id + "' not found in database");
        }
    }

    /**
     * Removes all documents in the collection from the database
     */
    public void clear() {
        collection.remove(new BasicDBObject());
    }
}
