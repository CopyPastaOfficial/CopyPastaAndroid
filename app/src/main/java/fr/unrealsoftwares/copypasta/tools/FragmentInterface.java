package fr.unrealsoftwares.copypasta.tools;

/**
 * The fragments implements this interface to facilitate the link between the
 * toolbar of an activity and the fragment
 */
public interface FragmentInterface {

    /**
     * @return The symbolic name of the fragment
     */
    public String getName();

    /**
     * Name of the String resource id called by the activity to display the text in the toolbar
     * @return String resource id
     */
    public int getTitleId();

}
