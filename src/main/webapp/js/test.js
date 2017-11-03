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

    if (document.contains(document.getElementById("searchWrapper"))) {
            document.getElementById("searchWrapper").remove();
    }

    $.getJSON( "/SearchEngine/search/json/" + input.value, function( data ) {
      console.log(data);
      var items = [];
      $.each( data, function( key, val ) {
        var title = val.title
        items.push( "<li style='font-size:20px' value='" + val.mongoId + "'><a href=/SearchEngine/search/index/result/" + val.mongoId + ">" + title + "</a></li>" );
      });
     
//      $( "<ul/>", {
//        "class": "myList",
//        "id": "movieList",
//        html: items.join( "" )
//      }).appendTo( "body" );

        var container = document.createElement('div');
        container.id = "searchWrapper";

        $('body').append(container);

        var html = '<ul class="suggestions">';

        for(var i=0; i<items.length; i++){
            html+= items[i];
        }

        html+= '</ul>';

        $('#searchWrapper').append(html);
    });

    // Clear the searchbox
    // $("#searchBox").val("");
}