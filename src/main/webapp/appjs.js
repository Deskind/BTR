

//variables
var ul;
var tokens = [];
var tradingSystems = [];

window.setInterval(function(){
    getBalances();
}, 120000);

$(document).ready(function(){
    ul = document.getElementById("traders_list");
    showAllTraders();
    
    window.setTimeout(function(){
        getBalances();
    }, 20000);
    
//    $("#newTraderToggleDiv, #tradersListToggleDiv").hide();
//    
//    $("#newTraderToggle").click(function(){
//        $("#newTraderToggleDiv").toggle();
//    });
//    
//    $("#tradersListToggle").click(function(){
//        $("#tradersListToggleDiv").toggle();
//    });
    
});

function showHideTraderData(token){
    var element = document.getElementById(token+"_addTsButton");
    if(element.style.display !== 'none')
    {
        document.getElementById(token+"_addTsButton").style.display = 'none';
        document.getElementById(token+"_delTsButton").style.display = 'none';
        document.getElementById(token+"_addTsInput").style.display = 'none';
        document.getElementById(token+"_traderTsList").style.display = 'none';
    }else{
        document.getElementById(token+"_addTsButton").style.display = 'block';
        document.getElementById(token+"_delTsButton").style.display = 'inline';
        document.getElementById(token+"_addTsInput").style.display = 'block';
        document.getElementById(token+"_traderTsList").style.display = 'block';
    }
}

//command to by contracts (for test)
function buy10(howMuchToBuy){
	var currentTime = new Date().getTime();
		
		$.ajax({
			cache: false,
			url: "trader?action=go&type=CALL&duration=1&duration_unit=m&symbol=R_50&tsName=t1",
			success: function (data) {
	            writeMessage("Запросы отправлены без задержки...");
	        }
		});
		
//		while (currentTime + 300 >= new Date().getTime()) {}
		
		$.ajax({
			cache: false,
			url: "trader?action=go&type=PUT&duration=1&duration_unit=m&symbol=R_50&tsName=t1",
			success: function (data) {
	            writeMessage("Запросы отправлены без задержки...");
	        }
		});
		
//		while (currentTime + 600 >= new Date().getTime()) {}
		
		$.ajax({
			cache: false,
			url: "trader?action=go&type=CALL&duration=1&duration_unit=m&symbol=R_50&tsName=t2",
			success: function (data) {
	            writeMessage("Запросы отправлены без задержки...");
	        }
		});
		
//		while (currentTime + 900 >= new Date().getTime()) {}
		
		$.ajax({
			cache: false,
			url: "trader?action=go&type=PUT&duration=1&duration_unit=m&symbol=R_50&tsName=t2",
			success: function (data) {
	            writeMessage("Запросы отправлены без задержки...");
	        }
		});
		
//		while (currentTime + 1200 >= new Date().getTime()) {}
		
		$.ajax({
			cache: false,
			url: "trader?action=go&type=PUT&duration=2&duration_unit=m&symbol=R_50&tsName=t1",
			success: function (data) {
	            writeMessage("Запросы отправлены без задержки...");
	        }
		});
		
//		while (currentTime + 1500 >= new Date().getTime()) {}
		
		$.ajax({
			cache: false,
			url: "trader?action=go&type=CALL&duration=2&duration_unit=m&symbol=R_50&tsName=t1",
			success: function (data) {
	            writeMessage("Запросы отправлены с задержкой...");
	        }
		});
		
		$.ajax({
			cache: false,
			url: "trader?action=go&type=CALL&duration=2&duration_unit=m&symbol=R_50&tsName=t2",
			success: function (data) {
	            writeMessage("Запросы отправлены с задержкой...");
	        }
		});
		
		$.ajax({
			cache: false,
			url: "trader?action=go&type=PUT&duration=2&duration_unit=m&symbol=R_50&tsName=t2",
			success: function (data) {
	            writeMessage("Запросы отправлены с задержкой...");
	        }
		});
}

function addNewTrader(token, name){
    //check is such trader already exists    
    for(i = 0; i < tokens.length;i++){
        if(tokens[i] === token){
            writeMessage("Such token alredy exists");
            return;
        }
    }
    
    //add new data to database
    if(token !== "" && name !== ""){
        $.ajax({
            url: "trader?action=addTrader&token="+token+"&name="+name,
            cache: false,
            success: function (data) {
                var tsList = [];
                addTraderToList(token, name, tsList);

                //add token to tokens array
                tokens.push(token);
                
                writeMessage("Трейдер добавлен");
            },
            error: function (){
                writeMessage("Не удалось добавить нового трейдера");
            }
        });
            
    }else{
        window.alert("Имя или токен не заполнены");
    }  
    
}

function addTsToTrader(token){
   var userInput = document.getElementById(token+"_addTsInput");
   
    if(userInput.value !== ""){
        $.ajax({
            url: "trader?action=addTsToTrader&token="+token+"&tsName="+userInput.value,
            cache: false,
            success: function (data) {
                console.log(data);
                //if "failure"
                if(data !== "success"){
                    writeMessage("Торговая система успешно добавлена");
                }else{//if "success"
                    //finding list for particular trader
                    var tsListElement = document.getElementById(token+"_traderTsList");

                    //create UI elements
                        //ts name
                        var tsName = document.createElement("h5");
                        tsName.setAttribute("style", "display: inline");
                        tsName.innerHTML = userInput.value;

                        //lot input
                        var tsLot = document.createElement("input");
                        tsLot.setAttribute("type", "number");
                        tsLot.setAttribute("value", "0.0");
                        tsLot.setAttribute("step", "0.1");
                        tsLot.setAttribute("style", "width: 60px; margin-left: 10px; margin-right: 10px");

                        //activity checkbox
                        var activityCheckbox = document.createElement("input");
                        activityCheckbox.setAttribute("type", "checkbox");
                        activityCheckbox.checked = false;

                        //save button
                        var saveButton = document.createElement("input");
                        saveButton.setAttribute("type", "button");
                        saveButton.setAttribute("value", "SAVE");
                        saveButton.setAttribute("style", "margin-left: 20px");


                    //add elements as list children
                    tsListElement.appendChild(tsName);
                    tsListElement.appendChild(tsLot);
                    tsListElement.appendChild(activityCheckbox);
                    tsListElement.appendChild(saveButton);
                        //brake line
                        tsListElement.appendChild(document.createElement("br"));

                    //EVENT LISTENERS
                    saveButton.addEventListener("click", function(){
                        writeMessage(token + "  " + tsLot.value + "  " + activityCheckbox.checked);
                        updateTraderTs(token, tsName.innerHTML, tsLot.value, activityCheckbox.checked);
                    });

                    //clear addTsInput input field
                    document.getElementById(token+"_addTsInput").value = "";
                }
            }
        });        
    }else{
        window.alert("Имя ТС не может быть пустым !!!");
        return;
    }
    
//    item.setAttribute("style", "color:green");
//    traderTsList.appendChild(item);
//    userInput.value = "";
}

function addNewTradingSystem(name){
    //name must not be empty
    if(name !== ""){
        //trading system array must not contain such trading system name
        if(tradingSystems.indexOf(name) < 0){
            //trying to add new ts to database
            $.ajax({
                cache: false,
                url: "trader?action=newTS&name="+name,
                success: function (data) {
                    //case of 'success' response
                    if(data === "success"){
                        //create elements
                        var element = document.createElement("li");
                        var list = document.getElementById("systems_list");
                        //prepare
                        element.innerHTML = name;
                        //add child to list
                        list.appendChild(element);
                    }else{//case of 'fail' response
                        window.alert("Не удалось добавить торговую систему...");
                    }
                }
            });
            //add ts name to array
            tradingSystems.push(name);
        }else{
            window.alert("Торговая система с таким именем уже существует...");
        }
    }else{
        window.alert("Имя новой торговой системы не может быть пустым...");
    }
        
}

function addTraderToList(token, name, tsArr){
    //string representation tsArr
    var ts = "=>";
    
   
    for(var k = 0 ; k < tsArr.length; k++){
        var element = tsArr[k];
        ts = ts.concat(element.name);
        ts = ts.concat("---");
        ts = ts.concat(element.lot+"$");
        ts = ts.concat("-");
        if(element.active === true){
            ts = ts.concat('ВКЛ');
        }else{
            ts = ts.concat('ВЫКЛ');
        }
        
        ts = ts.concat(" => ");
    }
    
    
//    var li = document.createElement("li");
//    li.setAttribute("id", token);
//    li.appendChild(document.createTextNode("" +name + "(" + token + ")"+ts));
//    li.classList.add("trader_li");
    
    var li = document.createElement("li");
    li.setAttribute("id", token);
    
    var link = document.createElement("a");
    link.setAttribute("href", "userdetails.html?name=" + name + "&token=" + token);
    link.innerHTML = name;
    
    var span = document.createElement("span");
    span.innerHTML = "  (" + token + ")" + ts;
    
    
    li.appendChild(link);
    li.appendChild(span);
    li.classList.add("trader_li");
    
    var balance = document.createElement("h4");
    balance.setAttribute("id", token+'_balance');
    balance.setAttribute("style", "color:green");
    balance.innerHTML = "Balance";
    
    var addTsBtn = document.createElement("input");
    addTsBtn.setAttribute("type", "button");
    addTsBtn.setAttribute("value", "Add TS");
    addTsBtn.setAttribute("id", token+"_addTsButton");
    addTsBtn.setAttribute("style", "display: inline; margin-top: 10px;");
    
    var delTsBtn = document.createElement("input");
    delTsBtn.setAttribute("type", "button");
    delTsBtn.setAttribute("value", "Del TS");
    delTsBtn.setAttribute("id", token+"_delTsButton");
    delTsBtn.setAttribute("style", "display: inline; margin-top: 10px;");
    
    var addTsInput = document.createElement("input");
    addTsInput.setAttribute("type", "text");
    addTsInput.setAttribute("id", token+"_addTsInput");
    addTsInput.setAttribute("placeholder", "Поле для добавления/удаления ТС");
    addTsInput.setAttribute("style", "display: block; margin-top: 10px; margin-bottom: 20px; width: 250px");
    
    var tsList = document.createElement("ul");
    tsList.setAttribute("id", token+"_traderTsList");
    tsList.className = "btn_new_line";
    tsList.setAttribute("style", "margin-bottom:20px;");
    
    //EVENT LISTENERS
    li.addEventListener("click", function(){
        showHideTraderData(token);
    });

    addTsBtn.addEventListener("click", function(){
        addTsToTrader(token);
    });
    
    delTsBtn.addEventListener("click", function(){
        delTraderTs(token, addTsInput.value);
    });
    
    //add trading systems to trader
    if(tsArr !== undefined){
        for(var i = 0; i < tsArr.length; i++){
            plusTs(tsArr[i], token, tsList);
        }
    }
    
    //APPEND CHILDREN TO MAIN LIST
    ul.appendChild(li);
    ul.appendChild(balance);
    ul.appendChild(addTsBtn);
    ul.appendChild(delTsBtn);
    ul.appendChild(addTsInput);
    ul.appendChild(tsList);
    
    //wrap trader's data
    showHideTraderData(token);
   
}

function plusTs(tsElement, token, tsList){
    //create UI elements
    
            var tsListItemElement = document.createElement("li");
            tsListItemElement.setAttribute("id", token+"_tsList_"+tsElement.name);
    
            //ts name
            var tsName = document.createElement("h5");
            tsName.setAttribute("style", "display: inline");
            tsName.innerHTML = tsElement.name;

            //lot input
            var tsLot = document.createElement("input");
            tsLot.setAttribute("type", "number");
            tsLot.setAttribute("value", "0.0");
            tsLot.setAttribute("step", "0.1");
            tsLot.setAttribute("style", "width: 60px; margin-left: 10px; margin-right: 10px");
            tsLot.value = tsElement.lot;

            //activity checkbox
            var activityCheckbox = document.createElement("input");
            activityCheckbox.setAttribute("type", "checkbox");
            if(tsElement.active === true){
                activityCheckbox.checked = true;
            }else{
                activityCheckbox.checked = false;
            }

            //save button
            var saveButton = document.createElement("input");
            saveButton.setAttribute("type", "button");
            saveButton.setAttribute("value", "SAVE");
            saveButton.setAttribute("style", "margin-left: 20px");

        //add elements as list children
        tsListItemElement.appendChild(tsName);
        tsListItemElement.appendChild(tsLot);
        tsListItemElement.appendChild(activityCheckbox);
        tsListItemElement.appendChild(saveButton);
            //brake line
            tsListItemElement.appendChild(document.createElement("br"));
            
        //add to tsList
        tsList.appendChild(tsListItemElement);

        //EVENT LISTENERS
        saveButton.addEventListener("click", function(){
            writeMessage(token + "  " + tsLot.value + "  " + activityCheckbox.checked);
            updateTraderTs(token, tsElement.name, tsLot.value, activityCheckbox.checked);
        });
        
}

function printAllTraders(){
    $.ajax({
        cache: false,
        url: "trader?action=printAllTraders",
        success: function (data, textStatus, jqXHR) {
            console.log(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            window.alert("Не удалось отобразить всех трейдеров");
        }
    });
}

function changeTraderState(token, isActive){
    $.ajax({
            cache: false,
            url: 'trader?action=changeTraderState&token='+token+'&isActive='+isActive+'&time='+new Date().getTime(),
            success: function(data) {
              document.getElementById("result").innerHTML = data;
            }
        });
}

function changeTsState(token, name, stateToSet, liElement){
    $.ajax({
        cache: false,
        url: 'trader?action=changeTsState&token='+token+'&tsName='+name+'&stateToSet='+stateToSet, 
        success: function (data) {
            if(liElement.style.color === "red"){
                liElement.style.color = "green";
            }else{
                liElement.style.color = "red";
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            writeMessage('Servlet action changeTsState => problem to change ts state');
        }
    });

    
}

function getBalances(){
    console.log("getBalances() call");
    $.ajax({
        cache: false,
        url: "trader?action=getAllBalances",
        success: function (data, textStatus, jqXHR) {  
            var balance = 0;
            var token = "";
            for(var i = 0 ; i < data.length; i ++){
                token = data[i].token;
                balance = data[i].balance;
                document.getElementById(token+"_balance").innerHTML = "Balance: " + balance;
            }
        }
    });
    
}

function test(){
    $.ajax({
        cache: false,
        url: "trader?action=test"
    });
}

function delTraderTs(token, tsName){
    if(tsName === ""){
        window.alert("Имя не может быть пустым");
    }else{
        $.ajax({
            cache: false,
            url: 'trader?action=delTraderTs&token='+token+'&tsName='+tsName,
            success: function (data, textStatus, jqXHR) {
                console.log(data);
                var list = document.getElementById(token+"_traderTsList");
                var itemsArr = list.getElementsByTagName("li");
                for(i = 0 ; i < itemsArr.length; i++){
                    if(itemsArr[i].innerHTML.includes(tsName)){
                        list.removeChild(itemsArr[i]);
                    }
                }
                writeMessage(data);
            },
            error: function(data){
                window.alert('Не удалось удалить ТС');
            }
        });
    }
}

function delTrader(token){    
    if(token !== ""){
        $.ajax({
        url: 'trader?action=delTrader&token='+token,
        success: function(data) {
            if(data === "success"){
                writeMessage("Трейдер с токеном удален...");
                ul.removeChild(document.getElementById(token));
                ul.removeChild(document.getElementById(token+'_balance'));
                ul.removeChild(document.getElementById(token+"_addTsButton"));
                ul.removeChild(document.getElementById(token+"_delTsButton"));
                ul.removeChild(document.getElementById(token+"_addTsInput"));
                ul.removeChild(document.getElementById(token+"_traderTsList"));
            }else{
                window.alert("Не удалось удалить трейдера ...");
            }
    	}
        });
    }else{
    	window.alert("Токен не может быть пустым");
    }
    
}

function updateTraderTs(token, tsName, lot, active){
    $.ajax({
        cache: false,
        url: "trader?action=updateTraderTs&token="+token+"&tsName="+tsName+"&lot="+lot+"&active="+active,
        success: function (data, textStatus, jqXHR) {
            console.log(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            window.alert("Серверу не удалось обновить торговую систему...");
        }
    });
}

function showAllTraders(){
    
    //clear old list
    ul.innerHTML = "";
    
    //clear an array of traders
    tokens = [];
    
    //getting all  trader from database
    $.ajax({
        cache: false,
        url: 'trader?action=allTraders',
        success: function(data) {
            console.log(data);
            for(var i = 0; i < data.length; i++){
                //getting data
                var trader = data[i];
                var name = trader.name;
                var token = trader.token;
                var tsList = trader.tsListDTO;
                
                
                //add token to arrray
                tokens.push(token);
//                
                addTraderToList(token, name, tsList);                
//                
//                showHideTraderData(token);
//
            }
            
        }
    });

}

function setBet(token, value){
        window.alert("User with token :" + token + " makes lot : " + value);
        $.ajax({
            cache: false,
            url: 'trader?action=setLot&token='+token+'&time='+new Date().getTime()+"&bet="+value,
            success: function(data) {
              document.getElementById("result").innerHTML = data;
            }
        });
}

function setMinimalPayout(newPayoutValue){
    $.ajax({
            cache: false,
            url: 'trader?action=setMinimalPayout&payoutValue='+newPayoutValue,
            success: function(data) {
              window.alert("Новый размер выплаты: " + newPayoutValue);
              document.getElementById("payoutInput").value = newPayoutValue;
            }
        });
    
}

function sortTraders(){
    console.log("+++Sort traders call...");
    $.ajax({
        url: 'trader?action=sortTraders',
        cache: false
    });
}

function startTradingProcess(){
    
    var appIDs = document.getElementById("app_id").value;
    
    if(appIDs === ""){
        window.alert("AppId is empty !!!");
        return ;
    }
    console.log(appIDs);
    $.ajax({
            cache: false,
            url: 'manager?action=start&appIDs='+appIDs,
            success: function(data) {
              document.getElementById("result").innerHTML = data;
            }
        });
}

function stopTradingProcess(){
    $.ajax({
            cache: false,
            url: 'manager?action=stop',
            success: function(data) {
              console.log(data);
              writeMessage(data);
            }
        });
}





function writeMessage(message){
    document.getElementById("result").innerHTML = message;
}