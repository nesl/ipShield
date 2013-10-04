$(document).ready(function() {
    $('#data').dataTable( {
        "aaData": [
            [ "Accelerometer", "Transportation mode", "still, walk, run", "93.6%", "[1]" ],
            [ "Accelerometer, GPS", "Transportation mode", "still, walk, run, drive, bike", "93.6%", "[1]" ],
            [ "Accelerometer, Gyroscope", "Onscreen Taps", "Location of taps on phone screen", "90%", "[2]" ],
            [ "Accelerometer, Gyroscope", "Onscreen Taps", "Text entered on phone", "80%", "[2]" ],
            [ "Accelerometer", "Device placement", "bag, ear, hand, pocket", "99.6%", "[3]" ],
            [ "Accelerometer, Microphone, Bluetooth", "Emotion", "happy, sad, fear, anger, neutral", "--", "[4]" ],
        ],
        "aoColumns": [
            { "sTitle": "Sensor" },
            { "sTitle": "Inference Category" },
            { "sTitle": "Context" },
            { "sTitle": "Accuracy"},
            { "sTitle": "Reference"}
        ], 
        "aaSorting": [[4, 'asc']]
    } );   

    $('#ref').dataTable( {
        "aaData": [
            [ "[1]", "Using Mobile Phones to Determine Transportation Modes", "ACM TOSN" ],
            [ "[2]", "TapPrints: Your Finger Taps Have Fingerprints", "MobiSys'13" ],
            [ "[3]", "Online Pose Classiﬁcation and Walking Speed Estimation using Handheld Devices", "UbiComp'12" ],
            [ "[4]", "EmotionSense: a mobile phones based adaptive platform for experimental social psychology research", "UbiComp'10" ]
        ],
        "aoColumns": [
            { "sWidth": "5%" , "sTitle": "No." },
            { "sWidth": "85%" , "sTitle": "Name" },
            { "sWidth": "10%" , "sTitle": "Venue" }
        ],
        "bAutoWidth": false,
        "aaSorting": [[0, 'asc']]
        
    } ); 
} );
