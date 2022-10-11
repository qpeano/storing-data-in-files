package data;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DataStorage {

    /* Fields */

    private File file; // where all data units are stored
    private ArrayList<DataUnit> units; // all the individual units
    private boolean isEmpty; // indicator for a few methods

    /* Methods - constructor */

    /**
     * constructor
     *
     * @param path string rep. of the path to the storage file
     * @throws IOException if something goes wrong while retrieving data units from storage file,
     * or if something happens while creating file
     */
    public DataStorage(String path) throws IOException {

        this.file = new File(path); // makes connection to file
        this.file.createNewFile(); // creates file if it does not exist
        this.units = new ArrayList<>();
        this.isEmpty = !(this.hasContent(this.file)); // to check if file has content, negated to suit use of field

        if (!this.isEmpty()) { // if file is not empty all data is extracted and written to list of units

            this.extract(this.file, this.units);
        }
    }

    /* Methods - internal */

    /**
     * Method is used check if a file has any content whatsoever, used in conjuntion with this.extract()
     *
     * @param f is the file used to store the data units
     * @throws IOException is something goes wrong while reading file
     */
    private boolean hasContent(File f) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(f)); // reading mechanism for file

        String line; // a line in the file
        boolean result = false;

        while ((line = br.readLine()) != null) { // result remains false until line doesn't hold an empty value

             result = true;
        }

        br.close(); // closes input connection to file
        return result;
    }

    /**
     * Method extracts all data from a file and fills a list of data units with said data
     *
     * @param f is the file used to store the data units
     * @param u is a list of data units
     * @throws IOException is something goes wrong while reading file, or if formatting is wrong
     */
    private void extract(File f, ArrayList<DataUnit> u) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(f)); // reading mechanism for file
        Pattern start = Pattern.compile("\s*[a-zA-Z0-9_)]+ \\{"); // marker for beginning of a new unit
        Pattern end = Pattern.compile("\s*\\}"); // marker for end of a unit
        String line; // a line in the file
        Matcher matchStart; // matches a line from file with start charcter
        Matcher matchEnd; // matches a line from file with end charcter
        int layer = 0; // the layer a line is in, layer being the number of nested data units a line is in
        int lineCounter = 0; // the number of a line in file
        DataUnit currentUnit = null; // the unit a line is in, if that line is in a unit at all

        while ((line = br.readLine()) != null) {  // do the following if the line doesn't hold an empty value

            matchStart = start.matcher(line);
            matchEnd = end.matcher(line);
            lineCounter++; // incremeted with every new line that is read in

            if (matchStart.matches()) { // if start character is found in line, add that line as a new unit

                int spaces = (line.replaceAll("[^ ]", "").length() - 1) / 4; // counts number of tabs => correlates to layer
                if (layer == 0) { // if line isn't already in a unit, create and add new unit with line

                    currentUnit = new DataUnit(line);
                    u.add(currentUnit);
                    layer++;
                }
                else if (layer < spaces + 1) { // if line is in a unit, create a new inner unit, and add that with the line

                    currentUnit = currentUnit.newInnerUnit(line);
                    layer++;
                }
            }
            else if (matchEnd.matches()) { // if end character is found in line, exit current unit (goto outer unit)

                if (layer > 1) { // if there exists outer units, exit current unit

                    layer--;
                    currentUnit = currentUnit.getOuterUnit();
                }
                else if (layer == 1) { // if there are no outer units, just alter layer field to match situation

                    layer--;
                }
                if (layer < 0) { // if a end character that belongs to no unit exists tell user, terminate program

                    String msg = "Formatting Error In Line: " + lineCounter + "\nIn Collection :" + this.getPath();
                    throw new IOException(msg);
                }
            }
            else { // if line has no special character, add it to the fragments of current unit

                if (layer == 0) { // if text is found outside a unit, tell user, terminate program

                    String msg = "Formatting Error In Line: " + lineCounter + "\nIn Collection :" + this.getPath();
                    throw new IOException(msg);
                }
                currentUnit.add(line); // add line if not
            }
        }

        br.close(); // close connection to file
    }

    /**
     * Method prints out all data units and their content to a file (the DataStorage)
     *
     * @param f the DataStorage file
     * @throws IOException if something goes wrong while writing to file
     */
    private void printDataUnits(File f) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(f)); // writing mechanism to file
        for (int i = 0; i < this.size(); i++) { // writes all data units to storing file

            if (i == this.size() - 1) {

                bw.write(this.units.get(i).toString());
            }
            else {

                bw.write(this.units.get(i).toString() + "\n");
            }
        }

        bw.close(); // closed connection to file
    }

    /* Methods - interface */

    /**
     * Method adds a new top-level unit to DataStorage
     *
     * @param label the name of the new unit
     * @throws IOException if something happens while writing to file
     */
     public void add(String label) throws IOException {

        this.units.add(new DataUnit(label)); // adds new unit
        this.printDataUnits(this.file); // prints all units out to file
        this.isEmpty = false; // changes status to NOT EMPTY, if file was empty before
     }

     /**
      * Method adds a new unit to DataStorage using the labels of other units
      *
      * @param labelList the labels of a nesting series (??)
      * @param label
      * @throws IOException
      */
    public void add(String label, String[] labelList) throws IOException {

        DataUnit current;
        for (int nestIndex = 0, nestIndex < labelList.length; i++) {

            if (this.getAllLabels().contains(labelList[nestIndex])) {

                
            }
        }
    }

    /* Methods - other */

    /**
     * Method makes a string representation of the entire DataStorage.
     * Used as a diagnostics tool to see if all other methods are working
     *
     * @return string representation of DataStorage
     */
    @Override // flag for complier
    public String toString() {

        if (!this.isEmpty) {

            String state = this.getPath() + ":\n"; // adds the path of collection

            for (int i = 0; i < this.size(); i++) { // goes through every unit and add their content to the string

                if (i == this.size() - 1) {

                    state += this.units.get(i).toString();
                }
                else {

                    state += this.units.get(i).toString() + "\n";
                }
            }

            return state;
        }
        else { // if Datastorage is empty, return nothing

            return null;
        }
    }

    /**
     * Method gives the number of top-level units that exists in a DataStorage
     *
     * @return number of top-level units
     */
    public int size() {

        return this.units.size();
    }

    /**
     * Method gives the string representation of the path of the DataStorage file
     *
     * @return path to DataStorage file
     */
    public String getPath() {

        return this.file.toString();
    }

    /**
     * Method checks if the file is empty
     *
     * @return value signaling emptiness
     */
    public boolean isEmpty() {

        return this.isEmpty;
    }
}
