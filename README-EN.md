French version [HERE](README.md)

# myCANAL Android Technical Test

This test will be conducted in 3 parts:
1. Display the detail page of a programme
2. Play a video stream
3. Explain all the choices made during the development

Please take as much time as you need to complete this technical test and do not hesitate to contact us with any questions.

As a result, you will have to share the source of the application via a Git project

During the review I will pay attention to:
- the use of git
- the respect of the architecture
- the implementation of the features
- If you want to go further, please do so in dedicated commits

Let's go! :)

## 0 - Overview of the application

### Functionally
- It displays a list of programmes (TV shows and films) at start-up

### Technically
- It has been developed in MVVM architecture following the principles of clean architecture
- The first page responds to the https://static.canal-plus.net/exoplayer/api/programmes.json api
- The apis are normally functional (contact us if this is not the case)
- The navigation in the application:
    - It uses a nav graph (nav_graph.xml)
    - The management of the click on the programs is not done, it will be up to you to find a solution to open the various pages
    - To know which page to open you can use the NavigateTo domain object included in ProgramUi

## 1 - View a detail page
When a program with the `NavigateTo` field of type `DetailPage` is clicked, a new page will be displayed which corresponds to the detail page.

To do this, you need to call the url contained in the `urlPage` field and then display :
- the title (`title`),
- the subtitle (`subtitle`),
- the image (`URLImage`),
- the summary (`summary`)
- a play button

## 2 - Play a programme from the list with ExoPlayer
When you click on a program with the `NavigateTo` field of type `QuickTime` you will have to launch the video stream directly with Exoplayer WITHOUT displaying the detail page. Quicktime programs correspond to unencrypted videos in HLS format

To retrieve the video stream to be played it will be necessary to call the url provided by the `urlMedias` field included in the `QuickTime` object which will return a `videoUrl` field with a url of a m3u8 file and the `encryption` field equal to `clear`.

**A little help :**
- Play a video with ExoPlayer: http://google.github.io/ExoPlayer/guide.html

## 3 - Explanation from the test (You can be succinct in your answers)

- Is there an error in the `ProgramView` class? If so, what functional bug does this produce?
- What is the risk of the OnClickMapper and ProgramMapper mappers? How could they be improved?
- Do you see any other improvements that could have been made?
- Did you encounter any difficulties in developing / understanding the project?
- Are there any improvements to your code that could have been made?
- How long did it take you to do this?