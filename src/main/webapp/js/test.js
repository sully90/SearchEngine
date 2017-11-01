// function myFunction() {
//   // Declare variables
//   var input, filter, table, tr, td, i;
//   input = document.getElementById("searchBox");
//   console.log(input.value);
// }

$("#searchBox").bind('keyup', function(event){ 
  if(event.keyCode == 13){ 
    event.preventDefault();
    $("#searchButton").click(); 
    // search(this.value);
  }
});

document.getElementById("searchButton").onclick = function() {
    input = document.getElementById("searchBox");
    console.log(input.value);

    if (document.contains(document.getElementById("movieList"))) {
            document.getElementById("movieList").remove();
    }

    $.getJSON( "/SearchEngine/search/json/" + input.value, function( data ) {
      console.log(data);
      var items = [];
      $.each( data, function( key, val ) {
        var title = val.title
        items.push( "<li id='" + key + "'>" + title + "</li>" );
      });
     
      $( "<ul/>", {
        "class": "myList",
        "id": "movieList",
        html: items.join( "" )
      }).appendTo( "body" );
    });

    // Clear the searchbox
    // $("#searchBox").val("");
}