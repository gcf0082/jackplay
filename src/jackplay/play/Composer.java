package jackplay.play;

import jackplay.JackLogger;
import jackplay.JackOptions;

import java.lang.instrument.Instrumentation;
import java.util.*;

// singleton
public class Composer {
    JackOptions options;
    Instrumentation inst;
    Map<String, Map<String, Map<Genre, Performer>>> program;
    LeadPerformer leadPerformer;

    public Composer(JackOptions options, Instrumentation inst) {
        this.options = options;
        this.inst =  inst;
        this.leadPerformer = new LeadPerformer(this);
        this.inst.addTransformer(leadPerformer, true);
        this.program = new HashMap<String, Map<String, Map<Genre, Performer>>>();
        PlayLogger.initialise(options);
    }

    public void logMethod(PlayGround playGround) throws Exception {
        if (isNewPlay(playGround, Genre.METHOD_LOGGING)) {
            addPlayToProgram(playGround, Genre.METHOD_LOGGING, null);
            this.performPlay(playGround.className);
        }
    }

    public void redefineMethod(PlayGround playGround, String methodSource) throws Exception {
        if (isNewPlay(playGround, Genre.METHOD_REDEFINE)) {
            addPlayToProgram(playGround, Genre.METHOD_REDEFINE, methodSource);
            this.performPlay(playGround.className);
        }
    }

    private boolean isNewPlay(PlayGround playGround, Genre genre) {
        boolean playExists =
                program.containsKey(playGround.className)
                && program.get(playGround.className).containsKey(playGround.methodLongName)
                && program.get(playGround.className).get(playGround.methodLongName).containsKey(genre);
        return !playExists || (genre == Genre.METHOD_REDEFINE);
    }

    private void addPlayToProgram(PlayGround playGround, Genre genre, String methodSource) {
        prepareProgram(playGround.className, playGround.methodLongName);
        Performer performer = createPerformer(playGround, genre, methodSource);
        program.get(playGround.className).get(playGround.methodLongName).put(genre, performer);
    }

    private Performer createPerformer(PlayGround playGround, Genre genre, String methodSource) {
        switch (genre) {
            case METHOD_LOGGING:
                return new LoggingPerformer(playGround);
            case METHOD_REDEFINE:
                return new RedefinePerformer(playGround, methodSource);
            default:
                return null;
        }
    }

    private void prepareProgram(String className, String methodLongName) {
        if (!program.containsKey(className)) {
            program.put(className, new HashMap<String, Map<Genre, Performer>>());
        }

        if (!program.get(className).containsKey(methodLongName)) {
            program.get(className).put(methodLongName, new HashMap<Genre, Performer>());
        }
    }

    private void performPlay(String className) throws Exception {
        Class c = Class.forName(className);
        if (c.getName().equals(className)) {
            if (inst.isModifiableClass(c) && inst.isRetransformClassesSupported()) {
                leadPerformer.setClassToPlay(c);
                inst.retransformClasses(c);
            } else {
                throw new Exception("class not modifiable:" + className);
            }
        }
    }

    public List<Performer> findPerformers(String className) {
        List<Performer> performers = new ArrayList<Performer>();
        Map<String, Map<Genre, Performer>> methodMap = program.get(className);
        for (Map<Genre, Performer> performerMap : methodMap.values()) {
            if (performerMap.containsKey(Genre.METHOD_REDEFINE)) {
                performers.add(performerMap.get(Genre.METHOD_REDEFINE));
            }
            if (performerMap.containsKey(Genre.METHOD_LOGGING)) {
                performers.add(performerMap.get(Genre.METHOD_LOGGING));
            }
        }

        return performers;
    }
}

