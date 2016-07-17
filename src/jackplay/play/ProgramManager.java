package jackplay.play;

import jackplay.play.domain.Genre;
import jackplay.play.domain.PlayGround;
import jackplay.play.performers.TracingPerformer;
import jackplay.play.performers.Performer;
import javassist.NotFoundException;

import java.util.*;

public class ProgramManager {
    Composer composer;
    Map<Genre, Map<String, Map<String, jackplay.play.performers.Performer>>> program;

    public void init(Opera opera) {
        program = new HashMap<Genre, Map<String, Map<String, Performer>>>();
        this.composer = opera.getComposer();
    }

    public void addPlayAsTracing(String methodLongName) throws Exception {
       this.addPlay(methodLongName, Genre.METHOD_LOGGING, null);
    }

    public void addPlayAsRedefinition(String methodLongName, String src) throws Exception {
        this.addPlay(methodLongName, Genre.METHOD_REDEFINE, src);
    }

    private void addPlay(String methodLongName, Genre genre, String src) throws Exception {
        PlayGround pg = new PlayGround(methodLongName);     // basic format validation
        validateMethodLocation(pg);                         // validate method existence
        if (!existsPlay(pg, genre)) {
            addToProgram(pg, genre, src);
            composer.performPlay(pg.className);
        }
    }

    private static void validateMethodLocation(PlayGround pg) throws NotFoundException {
        pg.locateMethod();
    }

    public void removeRedefinition(String methodLongName) {

    }

    public void removeRedefinitions(String className) {

    }

    private boolean existsPlay(PlayGround pg, Genre genre) throws NotFoundException {
        try {
            return program.get(genre).get(pg.className).containsKey(pg.methodLongName);
        } catch(NullPointerException npe) {
            return false;
        }
    }

    private synchronized void addToProgram(PlayGround pg, Genre genre, String methodSource) {
        prepareProgram(genre, pg.className);
        Performer performer = createPerformer(pg, genre, methodSource);
        program.get(genre).get(pg.className).put(pg.methodLongName, performer);
    }

    private synchronized void prepareProgram(Genre genre, String className) {
        prepareGenreMap(genre);
        prepareClassMap(genre, className);
    }

    private void prepareGenreMap(Genre genre) {
        if (!program.containsKey(genre))  program.put(genre, new HashMap<String, Map<String, jackplay.play.performers.Performer>>());
    }

    private void prepareClassMap(Genre genre, String className) {
        if (!program.get(genre).containsKey(className)) program.get(genre).put(className, new HashMap<String, jackplay.play.performers.Performer>());
    }

    private Performer createPerformer(PlayGround pg, Genre genre, String methodSource) {
        switch (genre) {
            case METHOD_LOGGING:
                return new TracingPerformer(pg);
            case METHOD_REDEFINE:
                return new jackplay.play.performers.RedefinePerformer(pg, methodSource);
            default:
                throw new RuntimeException("unknown genre " + genre.toString());
        }
    }

    void removeRedefinePerformers(String className) {
        program.get(Genre.METHOD_REDEFINE).remove(className);
    }

    public Collection<Performer> findPerformers(Genre genre, String className) {
        Map<String, Performer> methodMap = program.get(genre).get(className);
        return methodMap.values();
    }
}
