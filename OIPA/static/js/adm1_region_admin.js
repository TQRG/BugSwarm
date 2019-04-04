/**
 * Created with PyCharm.
 * User: vincentvantwestende
 * Date: 11/14/13
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function (){
   $('#update-adm1-region').click(function(){

       var btn = $('#update-adm1-region');

       $.ajax({
           type: "GET",
           data: ({'all': 1}),
           url: "/admin/geodata/adm1region/update-adm1-regions/",
           beforeSend: function() {
               btn.removeClass("btn-success");
               btn.addClass("btn-warning");
               btn.text("Updating...");
           },
           statusCode: {
               200: function() {
                   btn.addClass("btn-info");
                   btn.text("Updated");
               },
               404: function() {
                   btn.addClass("btn-danger");
                   btn.text("404 error...");
               },
               500: function() {
                   btn.addClass("btn-danger");
                   btn.text("500 error...");
               }
           }
       });
   });
});
