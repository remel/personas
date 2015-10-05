//App.jsx

var App  = React.createClass({displayName: 'App',

  	
  render: function() {
	  
	  return (
      <body>
        <div className="container-fluid"> 
        	(ready to rock)
        </div>
      </body>
	  );
  	}
});

window.onload = function() {
  React.render(
    <App/>,
    document.getElementById('body')
  );
};