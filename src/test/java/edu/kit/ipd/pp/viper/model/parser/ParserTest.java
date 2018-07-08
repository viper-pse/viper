package edu.kit.ipd.pp.viper.model.parser;

import org.junit.*;

import edu.kit.ipd.pp.viper.model.ast.Rule;
import edu.kit.ipd.pp.viper.model.ast.Variable;
import edu.kit.ipd.pp.viper.model.ast.Functor;
import edu.kit.ipd.pp.viper.model.ast.FunctorGoal;
import edu.kit.ipd.pp.viper.model.ast.Goal;
import edu.kit.ipd.pp.viper.model.ast.KnowledgeBase;
import edu.kit.ipd.pp.viper.model.parser.ParseException;
import edu.kit.ipd.pp.viper.model.parser.PrologParser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ParserTest {
    private PrologParser kbParser;
    private KnowledgeBase testBase;
    private KnowledgeBase emptyBase;
    
    @Before
    public void init() throws IOException, ParseException {
        String src = new String(Files.readAllBytes(Paths.get("src/test/resources/simpsons.pl")));
        this.kbParser = new PrologParser(src);
        
        this.testBase = new KnowledgeBase(Arrays.asList(
                    new Rule(new Functor("father", Arrays.asList(
                                Functor.atom("abe"), Functor.atom("homer")
                            )),
                                Arrays.asList()
                            ),
                    new Rule(new Functor("father", Arrays.asList(
                                Functor.atom("homer"), Functor.atom("bart")
                            )),
                                Arrays.asList()
                            ),
                    new Rule(new Functor("grandfather", Arrays.asList(
                                new Variable("X"), new Variable("Y")
                            )), Arrays.asList(
                                new FunctorGoal(new Functor("father", Arrays.asList(
                                    new Variable("X"), new Variable("Z")
                                ))),
                                new FunctorGoal(new Functor("father", Arrays.asList(
                                    new Variable("Z"), new Variable("Y")
                                )))
                            ))
                ));
        
        this.emptyBase = new KnowledgeBase(Arrays.asList());
    }
    
    @Test
    public void parseTest() throws ParseException {
        KnowledgeBase parsed = kbParser.parse();
        assertEquals(parsed, this.testBase);
        assertNotEquals(parsed, this.emptyBase);
    }
    
    @Test
    public void parseGoalListTest() throws ParseException {
        List<Goal> parsedOneRule = new PrologParser("grandfather(X, Y).").parseGoalList();
        assertEquals(new FunctorGoal(new Functor("grandfather", Arrays.asList(
                new Variable("X"), new Variable("Y")
            ))), parsedOneRule.get(0));
    }
}