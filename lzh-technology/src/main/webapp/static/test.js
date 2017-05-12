(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-98792007-1', 'auto');
ga('require', 'ecommerce');


function send(){
	

	var date = new Date();
	var year = date.getFullYear();
	var month = date.getMonth()+1;
	var day = date.getDate();
	var hour = date.getHours();
	var minute = date.getMinutes();
	var second = date.getSeconds();
	var orderid=year+'-'+month+'-'+day+' '+hour+':'+minute+':'+second;
	
	ga('ecommerce:addTransaction', {
	  'id': orderid,                  // Transaction ID. Required.
	  'affiliation': 'Acme Clothing',   // Affiliation or store name.
	  'revenue': '11.99',               // Grand Total.
	  'shipping': '5',                  // Shipping.
	  'tax': '1.29'                     // Tax.
	});
	ga('ecommerce:addItem', {
    'id': orderid,
    'name': 'Fluffy Pink Bunnies',
    'sku': 'DD23444',
    'category': 'Party Toys',
    'price': '11.99',
    'quantity': '1',
    'currency': 'GBP' // local currency code.
  });
	ga('ecommerce:send');
    ga('ecommerce:clear');
}