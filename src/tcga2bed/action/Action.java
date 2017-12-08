/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.action;

public abstract class Action {

    public abstract void execute(String[] args);

    public abstract void setParameters(Object obj);

}
