package ml2.logic.games;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.ecore.resource.Resource;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

import ml2.logic.games.GamePlayEngine.CheckResult;
import ml2.logic.games.GamePlayEngine.IncorrectBanning;
import ml2.logic.games.GamePlayEngine.MarkResult;

@Route()
public class GameDevView extends VerticalLayout {

    private Resource mm;
	private TextArea modelArea;
	private VerticalLayout clueList;
	private GameCardLayout gameCardsLayout;
	private GamePlayEngine gamePlay;

	public GameDevView() {
    	HorizontalLayout layout = new HorizontalLayout();
    	
    	VerticalLayout mm = createMetamodelArea();
    	VerticalLayout generatedGame = createGameGeneration();
    	
    	layout.add(mm);
    	layout.add(generatedGame);
    	
    	add(layout);
    }

	private VerticalLayout createGameGeneration() {
    	VerticalLayout layout = new VerticalLayout();

    	gameCardsLayout = new GameCardLayout();
    	gameCardsLayout.setResponsiveSteps(
    	        // Use one column by default
    	        new ResponsiveStep("0", 1),
    	        // Use two columns, if layout's width exceeds 500px
    	        new ResponsiveStep("100px", 3));
    	//gameCardsLayout.setSizeFull();
    	
    	
		clueList = new VerticalLayout();

		layout.add(gameCardsLayout);
		layout.add(clueList);
		Button button = new Button("Check");
		//Paragraph info = new Paragraph("");
		button.addClickListener(clickEvent -> {
			CheckResult checkResult = this.gamePlay.check();
			updateBoard(checkResult);
			//info.setText(infoText());
		});
		
		//layout.setAlignSelf(button, FlexComponent.Alignment.END);
		layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, button);
		
		layout.add(button);
		//layout.add(info);
		
		return layout;
	}
    
	private void updateBoard(CheckResult checkResult) {
		if (checkResult.isCorrect()) {
			gameCardsLayout.showCorrectAnswer(checkResult.getSolutionThing());
		}
		
		for (IncorrectBanning incorrectBanning : checkResult.getIncorrectBannings()) {
			gameCardsLayout.showIncorrectBanning(incorrectBanning.getThing());
			
			List<Clue> clues4Thing = gamePlay.getCluesFor(incorrectBanning.getThing());
			gameCardsLayout.showCorrectClues(incorrectBanning.getThing(), clues4Thing);
		}
		
		for (Thing thing : checkResult.getCorrectlyBannedThings()) {
			gameCardsLayout.showCorrectAnswer(thing);
		}
		
	}

	private VerticalLayout createMetamodelArea() {
    	VerticalLayout layout = new VerticalLayout();

		TextArea gameDescriptionArea = new TextArea();
    	gameDescriptionArea.setLabel("Puzzle description");
    	gameDescriptionArea.setWidth(400, Unit.PIXELS);
    	gameDescriptionArea.setMaxHeight(400, Unit.PIXELS);
    	//textArea.setMaxLength(charLimit);
    	//textArea.setValueChangeMode(ValueChangeMode.EAGER);
    	//textArea.addValueChangeListener(e -> {
    	//    e.getSource()
    	//            .setHelperText(e.getValue().length() + "/" + charLimit);
    	//});
    	gameDescriptionArea.setValue("""
#domain cars
#concept top = "blue", "green", "red"
#concept body = "blue", "green", "red"


Integer_min = 0
Integer_max = 31
# Integer = Set{ ... }

# String_max = 10
# String = Set{ ... }

# ------------------------------------------------------------------------ Thing
Thing_min = 9
Thing_max = 9

Thing_banned = Set{false,true}

# ThingProperty (thing:Thing, property:Property) - - - - - - - - - - - - - - - -
ThingProperty_min = 18
ThingProperty_max = 18

# --------------------------------------------------------------------- Property
Property_min = 6
Property_max = 6

Property_name = Set{'top','body'}
Property_value = Set{'red','green','blue'}

# ------------------------------------------------------------------------- Clue
Clue_min = 4
Clue_max = 4

# Clue_prem = Set{ ... }
Clue_negatedP = Set{false,true}
# Clue_conc = Set{ ... }
Clue_negatedC = Set{false,true}    			
    			""");
    	layout.add(gameDescriptionArea);

    	TextArea constraintsArea = new TextArea();
    	constraintsArea.setLabel("Additional constraints");
    	constraintsArea.setWidth(400, Unit.PIXELS);
    	constraintsArea.setMaxHeight(400, Unit.PIXELS);
    	constraintsArea.setValue("""
    	""");
    	layout.add(constraintsArea);
    	
    	/*
    	TextArea propertiesArea = new TextArea();
    	propertiesArea.setLabel("Bounds");
    	propertiesArea.setWidth(400, Unit.PIXELS);
    	propertiesArea.setMaxHeight(400, Unit.PIXELS);
    	propertiesArea.setValue("""
Table_min=1
Table_max=3
    	""");
    	layout.add(propertiesArea);
    	*/
    	
    	Button button = new Button("Generate game",
                event -> {
                	runFinder(gameDescriptionArea.getValue(), constraintsArea.getValue());
                });

    	layout.add(button);
	
    	return layout;
	}

	private class SelectableClue {

		private Clue clue;
		private GamePlayEngine gamePlay;

		private Span name;
		private HorizontalLayout item;
		private final int clueIndex;
		private ComponentEventListener<ClickEvent<Span>> listener;

		public SelectableClue(Clue c, GamePlayEngine gamePlay) {
			this.clue = c;
			this.gamePlay = gamePlay;
			this.clueIndex = this.gamePlay.getClues().indexOf(clue);
			
			HorizontalLayout item = new HorizontalLayout();
			this.name = createUnselectedBadge();
			
			NativeLabel desc = new NativeLabel(clue.getDescription());
			item.add(name);
			item.add(desc);

			this.item = item;
		}

		private Span createUnselectedBadge() {
			Span span = new Span(createIcon(VaadinIcon.QUESTION_CIRCLE), new Span("Clue #" + (clueIndex + 1)));
			span.getElement().getThemeList().add("badge");
			return span;
		}
		
		private Span createSelectedBadge() {
			Span span = new Span(createIcon(VaadinIcon.SELECT), new Span("Clue #" + (clueIndex + 1)));
			span.getElement().getThemeList().add("badge success");
			return span;
		}
		
		private void select() {
			Span newSpan = createSelectedBadge();
			this.item.replace(this.name, newSpan);
			this.name = newSpan;
			//addClickListener(listener);
		}

		private void unselect() {
			Span newSpan = createUnselectedBadge();
			this.item.replace(this.name, newSpan);
			this.name = newSpan;
			addClickListener(listener);
		}
		
		private Component getComponent() {
			return item;
		}
		
		public void addClickListener(ComponentEventListener<ClickEvent<Span>> listener) {
			this.listener = listener;
			name.addClickListener(e -> listener.onComponentEvent(e));
		}		
		
	}
	
	private void runFinder(String gameDescription, String oclProgram) {
		GameGenerator generator;
		try {
			generator = new GameGenerator(gameDescription, oclProgram);
			this.gamePlay = generator.getGamePlayEngine();
			
			List<SelectableClue> selectableClues = gamePlay.getClues().stream().map(c -> new SelectableClue(c, this.gamePlay)).collect(Collectors.toList());
			
			// Clues
			clueList.removeAll();
			selectableClues.forEach(clue -> {				
				clueList.add(clue.getComponent());
				
				clue.addClickListener(e -> {
					int oldClueId = this.gamePlay.getStatus().getCurrentClue();
					selectableClues.get(oldClueId).unselect();
					this.gamePlay.setCurrentClue(clue.clueIndex);
					selectableClues.get(clue.clueIndex).select();
				});
				
			});
			

			// Things			
			gameCardsLayout.setUp(gamePlay);		
			
			// Set up the game
			this.gamePlay.setCurrentClue(0);
			selectableClues.get(0).select();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public class GameCardLayout extends FormLayout {
		
		private Map<Thing, Span> correctMark = new HashMap<>();
		private Map<Thing, Span> incorrectMark = new HashMap<>();
		private Map<Thing, HorizontalLayout> markLayout = new HashMap<>();
		private Map<Thing, Span> solutionMark = new HashMap<>();
		private GamePlayEngine gamePlay;
		
		public void setUp(GamePlayEngine gamePlay) {
			this.gamePlay = gamePlay;
			this.removeAll();
			this.correctMark.clear();
			this.incorrectMark.clear();
			
			for(Thing thing: gamePlay.getThings()) {
				VerticalLayout thingLayout = new VerticalLayout();
				thingLayout.setMinHeight(50, Unit.PIXELS);

				HorizontalLayout markLayout4Thing = new HorizontalLayout();
				Span pending1 = createUnknownThingStatusBadge();
				markLayout.put(thing, markLayout4Thing);
				
				//pending1.addClickListener(event -> {
				//	System.out.println("Clicked!");
				//});
				markLayout4Thing.add(pending1);
				
				Span correctSpan = new Span(createIcon(VaadinIcon.CHECK));
				correctSpan.getElement().getThemeList().add("badge success");
				correctSpan.setVisible(false);
				markLayout4Thing.add(correctSpan);
				correctMark.put(thing, correctSpan);
				
				Span incorrectSpan = new Span(createIcon(VaadinIcon.CLOSE));
				incorrectSpan.getElement().getThemeList().add("badge error");
				incorrectSpan.setVisible(false);
				markLayout4Thing.add(incorrectSpan);
				incorrectMark.put(thing, incorrectSpan);
				
				thingLayout.add(markLayout4Thing);
				
				String s = gamePlay.getCluesFor(thing).stream().map(c -> (this.gamePlay.getClues().indexOf(c) + 1) + "").collect(Collectors.joining(", "));

				Span actualClueSpan = new Span("Actually banned by clue #" + s);
				actualClueSpan.getElement().getThemeList().add("badge primary");
				actualClueSpan.setVisible(false);
				thingLayout.add(actualClueSpan);
				solutionMark.put(thing, actualClueSpan);

				
				for(Pair<String, String> p : thing.getDescription().getElements()) {
					NativeLabel l = new NativeLabel(p.getLeft() + " = " + p.getRight());
					//l.setMinHeight(50, Unit.PIXELS);
					thingLayout.add(l);
				}
				
				//NativeLabel l = new NativeLabel(thing.getDescriptionAsString());
				//l.setMinHeight(50, Unit.PIXELS);
				//thingLayout.add(l);
				
				thingLayout.addClickListener(event -> {
					Component oldSpan = markLayout4Thing.getComponentAt(0);
					Span newSpan;
				
					if (gamePlay.isThingMarked(thing)) {
						gamePlay.unmarkThing(thing);
						newSpan = createUnknownThingStatusBadge();
						this.incorrectMark.get(thing).setVisible(false);
						this.correctMark.get(thing).setVisible(false);
						this.solutionMark.get(thing).setVisible(false);
					} else if (gamePlay.getUnmarkedThings() == 1) {
						newSpan = createSolutionThingStatusBadge();
					} else {
						MarkResult result = gamePlay.markThingBannedByCurrentClue(thing);
						
						if (result.thingIsCorrectlyBanned()) {
							//newSpan = createCorrectThingStatusBadge();							
						} else {
							//newSpan = createInvalidThingStatusBadge();
						}			
						
						newSpan = createBannedThingStatusBadge(gamePlay.getCurrentClue());
						
						if (result == MarkResult.NEW_CLUE) {
							//markCurrentClue();
							// TODO: Do something here
						}
					}
					
					markLayout4Thing.replace(oldSpan, newSpan);
				});
					
				this.add(thingLayout);
			}

		}

		public void showCorrectAnswer(Thing solutionThing) {
			Span span = this.correctMark.get(solutionThing);
			span.setVisible(true);
			/*
			Span correctSpan = new Span(createIcon(VaadinIcon.CHECK));
			correctSpan.getElement().getThemeList().add("badge success");
			correctSpan.setVisible(true);

			this.markLayout.get(solutionThing).replace(span, correctSpan);
			this.correctMark.put(solutionThing, correctSpan);
			*/
		}
		
		public void showIncorrectBanning(Thing solutionThing) {
			Span span = this.incorrectMark.get(solutionThing);

			Span incorrectSpan = new Span(createIcon(VaadinIcon.CLOSE));
			incorrectSpan.getElement().getThemeList().add("badge error");
			incorrectSpan.setVisible(true);
			
			this.markLayout.get(solutionThing).replace(span, incorrectSpan);	
			this.incorrectMark.put(solutionThing, incorrectSpan);
		}
		
		public void showCorrectClues(Thing thing, List<? extends Clue> clues4Thing) {		
			Span span = this.solutionMark.get(thing);
			span.setVisible(true);
		}
	}
	
	private Span createUnknownThingStatusBadge() {
		Span span = new Span(createIcon(VaadinIcon.QUESTION), new Span("Unknown"));
		span.getElement().getThemeList().add("badge");
		return span;
	}

	private Span createCorrectThingStatusBadge() {
		Span span = new Span(createIcon(VaadinIcon.CHECK), new Span("Selected"));
		span.getElement().getThemeList().add("badge success");
		return span;
	}
	
	private Span createInvalidThingStatusBadge() {
		Span span = new Span(createIcon(VaadinIcon.CLOSE), new Span("Selected"));
		span.getElement().getThemeList().add("badge error");
		return span;
	}


	private Span createBannedThingStatusBadge(int clueIndex) {
		Span span = new Span("Banned by clue #" + (clueIndex + 1));
		span.getElement().getThemeList().add("badge contrast");
		return span;
	}
	
	private Span createSolutionThingStatusBadge() {
		Span span = new Span("Solution!");
		span.getElement().getThemeList().add("badge success");
		return span;
	}
	
	
//	private void markCurrentClue() {
//		int currentClue = this.gamePlay.getStatus().getCurrentClue();
//		/*
//		for(int i = 0, len = this.clueList.getItems().size(); i < len; i++) {
//			MessageListItem item = this.clueList.getItems().get(i);
//			if (i == currentClue) {
//				item.setUserName("Clue #" + (currentClue + 1));
//				item.setUserColorIndex(1);			
//			} else {
//				item.setUserName("Clue #" + (currentClue + 1));				
//				item.setUserAbbreviation(null);
//				item.setUserColorIndex(0);
//			}
//		}
//		*/
//	}

	private Icon createIcon(VaadinIcon vaadinIcon) {
	    Icon icon = vaadinIcon.create();
	    icon.getStyle().set("padding", "var(--lumo-space-xs)");
	    return icon;
	}
	
}

