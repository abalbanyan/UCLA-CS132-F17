import java.util.regex.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Iterator;

class InheritanceTree {
    public final String classname;
    public final InheritanceTree parent;
    private LinkedList<InheritanceTree> leaves = new LinkedList<InheritanceTree>();

    /* Quickly looks up the node associated with the classname. */
    private static HashMap<String, InheritanceTree> lookupMap = new HashMap<String, InheritanceTree>();
    public static InheritanceTree lookup(String classname) {
        return lookupMap.get(classname);
    }

    /* Used to declare nodes. Set "parent" to null to declare the root node. */
    public InheritanceTree(String classname, InheritanceTree parent) {
        this.classname = classname;
        this.parent = parent;
    }

    /* Used to add a subnode to this node. Returns the new node itself. */
    public InheritanceTree addChild(String classname) {
        InheritanceTree newnode = new InheritanceTree(classname, this);
        this.leaves.add(newnode);
        this.lookupMap.put(classname, newnode);
        return newnode;
    }

    /* Return a list of this node's ancestors from youngest to oldest. */
    public LinkedList<InheritanceTree> getAncestors() {
        LinkedList<InheritanceTree> parents = new LinkedList<InheritanceTree>();
        InheritanceTree cur = this;
        while (cur.parent != null) {
            parents.add(cur);
            cur = cur.parent;
        }
        return parents;
    }

    /*
     * Perform a traversal of the InheritanceTree, returning the ancestors of classname.
     * Only call this function on the root of the tree.
     * Not really necessary since I implemented the mroe efficient getAncestors().
     */
    public LinkedList<String> getParentsOf(String classname) {
        return this.getParentsOf(classname, new LinkedList<String>());
    }
    private LinkedList<String> getParentsOf(String classname, LinkedList<String> path) {
        LinkedList<String> returnpath = null;
        LinkedList<String> newpath = new LinkedList<String>(path);
        newpath.add(this.classname);

        if (classname == this.classname) {
            return newpath;
        } else {
            for (InheritanceTree leaf : this.leaves) {
                returnpath = leaf.getParentsOf(classname, newpath);
                if (returnpath != null) {
                    return returnpath;
                }
            }
        }
        return null;
    }

    /*
     * Returns the node associated with the classname, or null if none exist.
     * Not really necessary since I implemented the more efficient lookup().
     */
    public InheritanceTree find(String classname) {
        if (this.classname == classname) {
            return this;
        } else {
            for (InheritanceTree leaf : this.leaves) {
                InheritanceTree node = leaf.find(classname);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }
}

class Typecheck {

    public String result;

    /*
     * Return the class name of a class.
     */
    Pattern class_identifier_pat;
    Pattern class_pat;
    public String classname(String classdec) {        
        Matcher matcher = this.class_pat.matcher(classdec.trim());
        if (matcher.find()) {
            // Return the identifier without the "class" keyword and any trailing spaces.
            return this.class_identifier_pat.matcher(matcher.group()).replaceFirst("");
        }
        return "";
    }
    
    /*
     * Given a classname, returns the immediate parent of that class, if one exists.
     */
    public String parent(String classname) {
        return null;
    }

    public Typecheck(String program) {
        // Compile regex.
        this.class_pat = Pattern.compile("^(class)[\\s]*[a-zA-Z0-9_]+");    
        this.class_identifier_pat = Pattern.compile("^(class)[\\s]*");

        this.result = classname(program);
    }

    public static void main(String [] args) {
        String program = "";
        Scanner input = new Scanner(System.in);
        while(input.hasNextLine()) {
            program += input.nextLine();
        }
        Typecheck typecheck = new Typecheck(program);
        System.out.println(typecheck.result);
    }
}