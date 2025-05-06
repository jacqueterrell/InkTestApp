# InkTestApp


### Getting Started
> Obtaining an Application Certificate
* To use the MyScript sdk I first had to go here to obtain a certificate https://atk.myscript.com/#/user/applicationList
* This certificate can be found in the 'com.example.inktestapp.certificate' directory
* Note: This certificate allows me to install this application on 20 devices. This means that you do not have to set up an account in order to run the app
* After making sure that my app packageName was added to the atk.myscript/#/user/application list, I was able to successfully run the sdk.
* I found in the MyScript Github documentation (https://github.com/myscript/interactive-ink-examples-android#:~:text=Building%20your%20own%20integration) that all that was needed for me to add the sdk to my project was to import the 'UIReferenceImplementation' directory into my existing project.

### Explanation Of Algorithms
> Summarization:
* When researching ways to summarize user's text I wanted to come up with an algorithm that would be easy to tweak as well as test around
* This led me down the path of a text rank inspired scoring algorithm. The idea here is simple.
1. Split the text into a list of sentences.
2. Try to filter each sentence into meaningful words by extracting out all stop words
3. assign each sentence a score using our algorithm where common words are more important
4. Sort the sentences by score
5. Return the top 'N' sentences
> Ink Enhancement
* To enhance the Ink I decided to leverage the MyInk SDK. To do this I created two methods 'setExistingCachedNoteUI' and 'setNewlyCreatedNoteUI'.
* Each of these methods updates internal values to ensure a better stylus experience  '-myscript-pen-width: 1.0; -myscript-pen-pressure-sensitivity'
* The pressure sensitivity is the dynamic one and depends on a setting the user can set. I focused on this setting because I found that my hand writing was much smoother with a thinker (more pressure) stylus ink.
* Eventually I would have liked to have a feature where I hover over the top portion of the Note where the handwritten note has been converted to text, and offer the user a list of word suggestions if the written text is not readable.


### Ink Signature Design Choice
> Stylus Cursor:
* I added a new cursor that resembles a stylus. This cursor is set as a stylus because I added a new switch toggle on the Note Creation screen where the user can use the Stylus point as a pen, or as an eraser. The stylus cursor informs them that they are in pen mode.
> Eraser Cursor
* Similar to the stylus cursor, but the eraser cursor informs the user that the Stylus now acts as an eraser and not a Pen. 
> Why I chose this design?
* I believe this embodies the idea of what ink-first AI-first is all about. By allowing the stylus to dynamically switch between a stylus or an eraser, we are prioritizing the stylus endpoint as well as having the AI feel as if it is a core feature and not an add-on.

### Testing
> ViewModel:
* My ViewModel test can be found in the 'NotesListViewModelTest' class. This class handles unit tests for two method calls
1. 'updateNotesAfterIndexChange' Ensures that the Room database properly updates each time our 'insertAll' method is called on our Dao.
2. 'getNotesFlow' verifies that the method properly returns the query of the local Room database to retrieve a State Flow.
> Util Test:
* I decided to create a unit test for the EditorUtil.testExtractKeySentences. This is because of the fact that the local summary algorithm having mixed results. 
* This test takes a sample Paragraph and verifies that the sentences are properly stripped out into a list and weighted correctly.

