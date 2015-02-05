package uk.ac.cam.echo.extrusionfinder.database;

public class PartNotFoundException extends Exception {
    public PartNotFoundException(String _id) {
        super("Part with id '" + _id + "' not found in database");
    }
}
