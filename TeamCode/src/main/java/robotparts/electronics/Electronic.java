package robotparts.electronics;


import utility.MainThreadAccess;

public abstract class Electronic {
    /**
     * Naming conventions for electronics
     * C -> continuous
     * P -> positional
     * O -> output
     * I -> input
     */
    /**
     */
    /**
     * Access represents the access the user has to use the electronic
     */
    protected final MainThreadAccess access = new MainThreadAccess();


    /**
    /**
     * Does the electronic have access?
     * @return isAllowed
     */

    /**
     * Halt the electronic (Used to stop motors)
     */
    public void halt(){}


}
