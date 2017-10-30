// function myFunction() {
//   // Declare variables
//   var input, filter, table, tr, td, i;
//   input = document.getElementById("searchBox");
//   console.log(input.value);
// }

document.getElementById("searchBox").onkeyup = function() {
    input = document.getElementById("searchBox");
    console.log(input.value);
}