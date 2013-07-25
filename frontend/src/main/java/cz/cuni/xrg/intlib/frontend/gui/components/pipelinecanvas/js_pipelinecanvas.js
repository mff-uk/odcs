/**
 * Javacript part of graph canvas.
 * Function name according to Vaadin specification
 *
 * @author Bogo
 * **/
cz_cuni_xrg_intlib_frontend_gui_components_pipelinecanvas_PipelineCanvas = function() {

	//remainder from JsLabel
	//used to insert container for kineticJS canvas
	var e = this.getElement();
	//e.innerHTML = "<div id='container' style='border:2px solid;width:1500px;height:800px'></div>";


	//provisional placement of DPU and connection "classes"
	/** 
	 * Class representing DPU for use on Canvas
	 *  
	 *  @param id
	 *  @param name
	 *  @param description
	 **/
	function Dpu(id, name, description) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.group = null;
		this.text = null;
		this.rect = null;
		this.tooltip = null;

		this.connectionFrom = [];
		this.connectionTo = [];

		this.getInfo = function() {
			return this.name + ' ' + this.description;
		};
	}

	/** 
	 * Class representing Connection between 2 DPUs on Canvas 
	 * 
	 *  @param id
	 *  @param from
	 *  @param to
	 *  @param line
	 *  @param actionBar
	 *  @param hitLine
	 **/
	function Connection(id, from, to, line, actionBar, hitLine) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.line = line;
		this.actionBar = actionBar;
		this.hitLine = hitLine;
		this.arrowLeft = null;
		this.arrowRight = null;
		this.dataUnitNameText = null;
	}

	/** RPC proxy for calling server-side methods from client **/
	var rpcProxy = this.getRpcProxy();

	/** DPUs and Connections collections**/
	var dpus = {};
	var connections = {};

	var lastPositionX = 0;
	var lastPositionY = 0;

	var selectedDpu = null;

	var scale = 1.0;

	/** Registering RPC for calls from server-side**/
	this.registerRpc({
		init: function() {
			init();
		},
		addNode: function(dpuId, name, description, type, x, y) {
			addDpu(dpuId, name, description, type, x, y);
		},
		addEdge: function(id, dpuFrom, dpuTo, dataUnitName) {
			addConnection(id, dpuFrom, dpuTo, dataUnitName);
		},
		updateNode: function(id, name, description) {
			updateDpu(id, name, description);
		},
		updateEdge: function(id, dataUnitName) {
			updateEdge(id, dataUnitName);
		},
		resizeStage: function(height, width) {
			resizeStage(height, width);
		},
		zoomStage: function(zoom) {
			zoomStage(zoom);
		},
		clearStage: function() {
			clearStage();
		}
	});

	//DoubleClick hack
	var lastClickedDpu = null;
	var lastClickedTime = null;


	//dragVariables
	var isDragging = false;
	var dragId = 0;
	var line = null;


	/** Pipeline states
	 * 	NORMAL_MODE - standard mode, DPUs can be dragged
	 *  NEW_CONNECTION_MODE - new connection is being created, line follows mouse
	 *  ...
	 * **/
	// Stage state constants
	var NEW_CONNECTION_MODE = "new_connection_mode";
	var NORMAL_MODE = "normal_mode";

	var stageMode = NORMAL_MODE;

	/** New connection related variables **/
	//Adding new connection
	var newConnLine = null;
	var newConnStart = null;

	/** Kinetic stage and layers accessed from different functions **/
	var stage = null;
	/** Layer with DPUs**/
	var dpuLayer = null;
	/** Connection layer **/
	var lineLayer = null;
	/** Mesage/debug layer **/
	var messageLayer = null;

	var addConnectionIcon = null;
	var removeConnectionIcon = null;
	var debugIcon = null;
	var detailIcon = null;

	var backgroundRect = null;

	/** Init function which builds entire stage for pipeline */
	function init() {

		stage = new Kinetic.Stage({
			container: 'container',
			width: 1600,
			height: 630
		});

		dpuLayer = new Kinetic.Layer();
		lineLayer = new Kinetic.Layer();
		messageLayer = new Kinetic.Layer();

		// MouseMove event on stage
		stage.on('mousemove', function() {
			if (isDragging) {
				// Takes care of repositioning connections on dragged DPU
				var mousePos = stage.getMousePosition();
				var x = mousePos.x / scale;
				var y = mousePos.y / scale;
				//writeMessage(messageLayer, 'x: ' + x + ', y: ' + y);
				moveLine(dragId, x, y);
			} else if (stageMode === NEW_CONNECTION_MODE) {
				// Repositioning new connection line
				var mousePosition = stage.getMousePosition();
				newConnLine.setPoints(computeConnectionPoints3(newConnStart.group, mousePosition.x / scale, mousePosition.y / scale));
				lineLayer.draw();
			}
		});

		// Redraws Connection layer after drag
		dpuLayer.on('draw', function() {
			lineLayer.draw();
		});

		//background layer for detection of mouse move on whole stage
		var backgroundLayer = new Kinetic.Layer();
		backgroundRect = new Kinetic.Rect({
			x: 0,
			y: 0,
			fill: '#fff',
			width: 1600,
			height: 630
		});

		stage.on('click', function(evt) {
			if (stageMode === NEW_CONNECTION_MODE) {
				// Cancels NEW_CONNECTION_MODE
				newConnLine.destroy();
				newConnLine = null;
				newConnStart = null;
				stageMode = NORMAL_MODE;
				lineLayer.draw();
			} else {
				setSelectedDpu(null);
			}
		});

		/*
		 stage.onkeydown = function(evt) {
		 writeMessage(messageLayer, evt.keyIdentifier)
		 
		 //46 identifier for Delete
		 if(evt.keyIdentifier == 46) {
		 //TODO: Delete DPU/Connection???
		 }
		 };
		 */
		backgroundLayer.add(backgroundRect);
		stage.add(backgroundLayer);


		// add the layers to the stage
		stage.add(lineLayer);
		stage.add(dpuLayer);
		stage.add(messageLayer);
		writeMessage(messageLayer, 'initialized');


		var basePath = "http://" + window.location.host + window.location.pathname;
		if (basePath.charAt(basePath.length - 1) !== '/') {
			basePath = basePath + '/';
		}
		var imgPath = "VAADIN/themes/IntLibTheme/img/";
		basePath = basePath + imgPath;

		addConnectionIcon = new Image();
		addConnectionIcon.src = basePath + "arrow_right_32.png";

		removeConnectionIcon = new Image();
		removeConnectionIcon.src = basePath + "TrashFull.png";

		debugIcon = new Image();
		debugIcon.src = basePath + "debug.png";

		detailIcon = new Image();
		detailIcon.src = basePath + "Gear.png";
	}

	/** 
	 * Function for moving connection lines after DPU is dragged 
	 * @param dpuId id of dpu which was moved
	 * @param x x coordinate of new position
	 * @param y y coordinate of new position
	 **/
	function moveLine(dpuId, x, y) {
		var dpu = dpus[dpuId];
		var dpuGroup = dpu.group;
		for (lineId in dpu.connectionFrom) {
			var conn = connections[dpu.connectionFrom[lineId]];
			var dpuTo = dpus[conn.to].group;
			var newPoints = computeConnectionPoints2(dpuGroup, dpuTo);
			conn.line.setPoints(newPoints);
			conn.arrowLeft.setPoints(computeLeftArrowPoints(newPoints));
			conn.arrowRight.setPoints(computeRightArrowPoints(newPoints));
			if (conn.dataUnitNameText !== null) {
				conn.dataUnitNameText.setPosition(computeTextPosition(newPoints, 100));
			}
		}
		for (lineId in dpu.connectionTo) {
			conn = connections[dpu.connectionTo[lineId]];
			var dpuFrom = dpus[conn.from].group;
			newPoints = computeConnectionPoints2(dpuFrom, dpuGroup);
			conn.line.setPoints(newPoints);
			conn.arrowLeft.setPoints(computeLeftArrowPoints(newPoints));
			conn.arrowRight.setPoints(computeRightArrowPoints(newPoints));
			if (conn.dataUnitNameText !== null) {
				conn.dataUnitNameText.setPosition(computeTextPosition(newPoints, 100));
			}
		}
	}

	/** 
	 * Writes message on given message layer 
	 * 
	 * @param messageLayer
	 * @param message
	 **/
	function writeMessage(messageLayer, message) {
//        var context = messageLayer.getContext();
//        messageLayer.clear();
//        context.font = '18pt Calibri';
//        context.fillStyle = 'black';
//        context.fillText(message, 10, 25);

		rpcProxy.onLogMessage(message);
	}

	/** Updates text in DPU visualization
	 *
	 * @param id ID of Dpu to update
	 * @param name new Dpu name
	 * @param description new Dpu description
	 */
	function updateDpu(id, name, description) {
		var dpu = dpus[id];
		dpu.text.setText(name + '\n\n' + description);
		dpu.rect.setHeight(dpu.text.getHeight());
		dpuLayer.draw();
	}

	/**
	 * Updates text in edge visualization
	 * 
	 * @param {type} id Id of edge to update
	 * @param {type} dataUnitName new DataUnit name
	 */
	function updateEdge(id, dataUnitName) {
		var con = connections[id];
		if (dataUnitName === null && con.dataUnitNameText !== null) {
			con.dataUnitNameText.destroy();
			con.dataUnitNameText = null;
			lineLayer.draw();
			return;
		}
		if (con.dataUnitNameText === null) {
			linePoints = con.line.getPoints();
			var points = [linePoints[0].x, linePoints[0].y, linePoints[1].x, linePoints[1].y];
			con.dataUnitNameText = createDataUnitNameText(id, dataUnitName, points);
		} else {
			con.dataUnitNameText.setText(dataUnitName);
		}
		lineLayer.draw();
	}

	function getDpuColor(type) {
		if (type === "EXTRACTOR") {
			return '#A6F22A';
		} else if (type === "TRANSFORMER") {
			return '#25A8C0';
		} else {
			return '#FF402D';
		}
	}

	function clearStage() {
		dpus.clear();
		connections.clear();
		stage.destroy();
		init();
	}
	
	function activateTooltip(dpu, text) {
		dpu.tooltip.getText().setText(text);
		var position = stage.getMousePosition();
		position.x = (position.x + 16) / scale;
		position.y = position.y / scale;
		dpu.tooltip.setPosition(position);
		dpu.tooltip.setVisible(true);
		dpuLayer.draw();
	}
	
	function deactivateTooltip(dpu) {
		dpu.tooltip.setVisible(false);
		dpuLayer.draw();
	}

	/** 
	 * Builds DPU object and creates its representations on the stage
	 * 
	 *  @param id ID of new Dpu
	 *  @param name Name of new Dpu
	 *  @param description
	 *  @param type
	 *  @param posX x coordinate of Dpu's position 
	 *  @param posY y coordinate of Dpu's position
	 **/
	function addDpu(id, name, description, type, posX, posY) {

		var dpu = new Dpu(id, name, description);

		// since this text is inside of a defined area, we can center it using align: 'center'
		// Text for DPU name and description
		var complexText = new Kinetic.Text({
			x: 0,
			y: 0,
			text: name + '\n\n' + description,
			fontSize: 10,
			fontFamily: 'Calibri',
			fill: '#555',
			width: 120,
			padding: 6,
			align: 'center'
		});

		// Graphical representation of DPU
		var fill = getDpuColor(type);
		var rect = new Kinetic.Rect({
			x: 0,
			y: 0,
			stroke: '#555',
			strokeWidth: 2,
			fill: fill,
			width: 120,
			height: complexText.getHeight(),
			shadowColor: 'black',
			shadowBlur: 5,
			shadowOffset: [5, 5],
			shadowOpacity: 0.2,
			cornerRadius: 5
		});

		// Group containing text and rect
		if (posX < 0) {
			var mousePos = null; //stage.getPointerPosition();// stage.getMousePosition();
			if (mousePos !== null) {
				posX = mousePos.x;
				posY = mousePos.y;
			} else {
				console.log("X: " + lastPositionX + " Y: " + lastPositionY);
				posX = lastPositionX - 261;
				posY = lastPositionY - 256;
			}
		} else {
			posX = posX * scale;
			posY = posY * scale;
		}

		var group = new Kinetic.Group({
			x: posX / scale,
			y: posY / scale,
			rotationDeg: 0,
			draggable: true
		});

		// Action bar on DPU
		var actionBar = new Kinetic.Group({
			x: rect.getWidth() - 16,
			y: 0,
			width: 16,
			height: 64,
			visible: false
		});

		// New Connection command
		var cmdConnection = new Kinetic.Image({
			x: 0,
			y: 0,
			image: addConnectionIcon,
			width: 16,
			height: 16,
			startScale: 1
		});

		cmdConnection.on('click', function(evt) {
			if (stageMode === NORMAL_MODE) {
				writeMessage(messageLayer, 'action bar clicked');
				var mousePosition = stage.getMousePosition();
				newConnLine = new Kinetic.Line({
					points: computeConnectionPoints3(group, mousePosition.x / scale, mousePosition.y / scale),
					stroke: '#555',
					strokeWidth: 1.5
				});
				stageMode = NEW_CONNECTION_MODE;
				newConnStart = dpu;
				writeMessage(messageLayer, 'Clicking on:' + dpu.name);
				lineLayer.add(newConnLine);
				lineLayer.draw();
				evt.cancelBubble = true;
			}
		});
		cmdConnection.on('mouseenter', function(evt) {
			activateTooltip(dpu, 'Create new edge');
			//evt.cancelBubble = true;
		});
		cmdConnection.on('mouseleave', function(evt) {
			deactivateTooltip(dpu);
			evt.cancelBubble = true;
		});
		actionBar.add(cmdConnection);

		// DPU Detail command
		var cmdDetail = new Kinetic.Image({
			x: 0,
			y: 16,
			image: detailIcon,
			width: 16,
			height: 16,
			startScale: 1
		});

		cmdDetail.on('click', function(evt) {
			writeMessage(messageLayer, 'DPU detail requested');
			rpcProxy.onDetailRequested(dpu.id);
			evt.cancelBubble = true;
		});
		cmdDetail.on('mouseenter', function(evt) {
			activateTooltip(dpu, 'Show detail');
			//evt.cancelBubble = true;
		});
		cmdDetail.on('mouseleave', function(evt) {
			deactivateTooltip(dpu);
			evt.cancelBubble = true;
		});
		actionBar.add(cmdDetail);

		// DPU Remove command
		var cmdRemove = new Kinetic.Image({
			x: 0,
			y: 48,
			image: removeConnectionIcon,
			width: 16,
			height: 16,
			startScale: 1
		});

		cmdRemove.on('click', function(evt) {
			writeMessage(messageLayer, 'DPU removed');
			removeDpu(dpu);
			rpcProxy.onDpuRemoved(dpu.id);
			evt.cancelBubble = true;
		});
		cmdRemove.on('mouseenter', function(evt) {
			activateTooltip(dpu, 'Remove DPU');
			//evt.cancelBubble = true;
		});
		cmdRemove.on('mouseleave', function(evt) {
			deactivateTooltip(dpu);
			evt.cancelBubble = true;
		});
		actionBar.add(cmdRemove);

		// Debug command
		var cmdDebug = new Kinetic.Image({
			x: 0,
			y: 32,
			image: debugIcon,
			width: 16,
			height: 16,
			startScale: 1
		});

		cmdDebug.on('click', function(evt) {
			writeMessage(messageLayer, 'Debug requested');
			rpcProxy.onDebugRequested(dpu.id);
			evt.cancelBubble = true;
		});
		cmdDebug.on('mouseenter', function(evt) {
			activateTooltip(dpu, 'Debug to this DPU');
			//evt.cancelBubble = true;
		});
		cmdDebug.on('mouseleave', function(evt) {
			deactivateTooltip(dpu);
			evt.cancelBubble = true;
		});
		actionBar.add(cmdDebug);



		group.add(rect);
		group.add(complexText);
		group.add(actionBar);

		// Handling the visibility of actionBar
		group.on('mouseenter', function() {
			if (stageMode === NORMAL_MODE) {
				actionBar.setVisible(true);
				actionBar.moveToTop();
				writeMessage(messageLayer, 'mouseentered');
				dpuLayer.draw();
				//	actionLayer.draw();
			}
		});

		group.on('mouseleave', function() {
			actionBar.setVisible(false);
			dpuLayer.draw();
			return;
			var pos = stage.getMousePosition();
			pos.x = pos.x / scale;
			pos.y = pos.y / scale;
			var groupPos = group.getPosition();

			if (pos.x < groupPos.x || pos.x > groupPos.x + group.getWidth() || pos.y < groupPos.y || pos.y > groupPos.y + group.getHeight()) {
				//clickRect.setVisible(false);
				writeMessage(messageLayer, 'mouseleft');

			}
		});

		// Registering for drag
		group.on('dragstart', function() {
			writeMessage(messageLayer, 'dragstart');
			isDragging = true;
			dragId = id;
			actionBar.setVisible(false);
		});
		group.on('dragend', function(evt) {
			writeMessage(messageLayer, 'dragend');
			isDragging = false;
			dragId = 0;
			var endPosition = group.getPosition();
			if (endPosition === null) {
				writeMessage(messageLayer, 'DPU removed - Out of Stage');
				removeDpu(dpu);
				rpcProxy.onDpuRemoved(dpu.id);
				evt.cancelBubble = true;
			} else {
				rpcProxy.onDpuMoved(dpu.id, parseInt(endPosition.x), parseInt(endPosition.y));
				writeMessage(messageLayer, 'x: ' + endPosition.x + ', y: ' + endPosition.y);
				actionBar.setVisible(true);
				dpuLayer.draw();
			}
		});

		// Creating new connection
		group.on('click', function(evt) {
			writeMessage(messageLayer, 'Clicked on Extractor');

			if (stageMode === NEW_CONNECTION_MODE) {
				newConnLine.destroy();
				newConnLine = null;
				stageMode = NORMAL_MODE;
				idFrom = newConnStart.id;
				idTo = dpu.id; //getDpuByPosition(stage.getMousePosition());
				if (idTo !== 0) {
					rpcProxy.onConnectionAdded(idFrom, parseInt(idTo));
				}
				newConnStart = null;
			} else if (stageMode === NORMAL_MODE) {
				if (evt.ctrlKey) {
					writeMessage(messageLayer, 'DPU removed - CTRL');
					removeDpu(dpu);
					rpcProxy.onDpuRemoved(dpu.id);
				} else if (evt.shiftKey) {
					writeMessage(messageLayer, 'New Edge - SHIFT');
					var mousePosition = stage.getMousePosition();
					newConnLine = new Kinetic.Line({
						points: computeConnectionPoints3(group, mousePosition.x / scale, mousePosition.y / scale),
						stroke: '#555',
						strokeWidth: 1.5
					});
					stageMode = NEW_CONNECTION_MODE;
					newConnStart = dpu;
					writeMessage(messageLayer, 'Clicking on:' + dpu.name);
					lineLayer.add(newConnLine);
					lineLayer.draw();

				} else {
					var now = Date.now();
					if (lastClickedDpu !== null && lastClickedTime !== null) {
						if (lastClickedDpu === group && now - lastClickedTime < 500) {
							lastClickedTime = null;
							writeMessage(messageLayer, 'Detail requested');
							rpcProxy.onDetailRequested(dpu.id);
							evt.cancelBubble = true;
							return;
						}
					}
					setSelectedDpu(dpu);
					lastClickedDpu = group;
					lastClickedTime = now;
				}
				if (!isDragging) {
					evt.cancelBubble = true;
				}
			}
		});

		dpu.rect = rect;
		dpu.text = complexText;
		dpu.group = group;
		dpu.tooltip = createTooltip('Tooltip');
		dpuLayer.add(dpu.tooltip);
		dpus[id] = dpu;
		dpuLayer.add(group);
		dpuLayer.draw();

		rpcProxy.onDpuMoved(id, parseInt(posX / scale), parseInt(posY / scale));
	}

	/** 
	 * Adds connection between 2 given DPUs 
	 *
	 * @param id ID of new connection
	 * @param from ID of connection's start Dpu 
	 * @param to ID of connection's end Dpu
	 * @param dataUnitName name of connection's DataUnit
	 **/
	function addConnection(id, from, to, dataUnitName) {

		var dpuFrom = dpus[from].group;
		var dpuTo = dpus[to].group;

		var linePoints = computeConnectionPoints2(dpuFrom, dpuTo);

		// Graphic representation of connection
		line = new Kinetic.Line({
			points: linePoints,
			stroke: '#555',
			strokeWidth: 1.5
		});

		var hitLine = new Kinetic.Line({
			points: linePoints,
			strokeWidth: 15,
			stroke: '#555',
			opacity: 0
		});

		hitLine.on('click', function(evt) {
			var ab = connections[id].actionBar;
			var pos = stage.getMousePosition();
			pos.x = (pos.x - 8) / scale;
			pos.y = (pos.y - 16) / scale;
			ab.setPosition(pos);
			ab.moveToTop();
			ab.setVisible(true);
			lineLayer.draw();
		});

		var lineArrowLeft = new Kinetic.Line({
			points: computeLeftArrowPoints(linePoints),
			stroke: '#555',
			strokeWidth: 1
		});

		var lineArrowRight = new Kinetic.Line({
			points: computeRightArrowPoints(linePoints),
			stroke: '#555',
			strokeWidth: 1
		});

		var dataUnitNameText = null;
		if (dataUnitName !== null) {
			dataUnitNameText = createDataUnitNameText(id, dataUnitName, linePoints);
		}

		// Action bar on Edge
		var actionBar = new Kinetic.Group({
			x: 0,
			y: 0,
			width: 16,
			height: 32,
			visible: false
		});

		var cmdName = new Kinetic.Image({
			x: 0,
			y: 0,
			image: detailIcon,
			width: 16,
			height: 16,
			startScale: 1
		});

		cmdName.on('click', function(evt) {
			rpcProxy.onDataUnitNameEditRequested(id);
			evt.cancelBubble = true;
		});

		actionBar.add(cmdName);

		// Delete command for connection
		var cmdDelete = new Kinetic.Image({
			x: 0,
			y: 16,
			image: removeConnectionIcon,
			width: 16,
			height: 16,
			startScale: 1
		});

		cmdDelete.on('click', function(evt) {
			removeConnection(id);
			evt.cancelBubble = true;
		});

		actionBar.add(cmdDelete);

		actionBar.on('mouseleave', function(evt) {
			actionBar.setVisible(false);
			lineLayer.draw();
		});

		var con = new Connection(id, from, to, line, actionBar, hitLine);
		con.arrowLeft = lineArrowLeft;
		con.arrowRight = lineArrowRight;
		connections[id] = con;
		dpus[from].connectionFrom.push(id);
		dpus[to].connectionTo.push(id);
		lineLayer.add(lineArrowLeft);
		lineLayer.add(lineArrowRight);
		lineLayer.add(actionBar);
		lineLayer.add(hitLine);
		lineLayer.add(line);
		if (dataUnitNameText !== null) {
			con.dataUnitNameText = dataUnitNameText;
			lineLayer.add(dataUnitNameText);
		}
		lineLayer.draw();
	}

	/** 
	 * Removes given connection 
	 * @param id id of connection to remove
	 **/
	function removeConnection(id) {
		var con = connections[id];
		var idx = dpus[con.from].connectionFrom.indexOf(id);
		dpus[con.from].connectionFrom.splice(idx, 1);
		idx = dpus[con.to].connectionTo.indexOf(id);
		dpus[con.to].connectionTo.splice(idx, 1);
		con.line.destroy();
		con.arrowLeft.destroy();
		con.arrowRight.destroy();
		con.actionBar.destroy();
		con.hitLine.destroy();
		connections[id] = null;
		rpcProxy.onConnectionRemoved(id);
		lineLayer.draw();
	}

	/** 
	 * Removes DPU and related connections 
	 * 
	 * @param dpu dpu to remove
	 **/
	function removeDpu(dpu) {
		var count = dpu.connectionFrom.length;
		for (var i = 0; i < count; i++) {
			removeConnection(dpu.connectionFrom[0]);
		}
		count = dpu.connectionTo.length;
		for (var i = 0; i < count; i++) {
			removeConnection(dpu.connectionTo[0]);
		}
		dpu.group.destroy();
		dpus[dpu.id] = null;
		dpuLayer.draw();
	}

	function setSelectedDpu(dpu) {
		if (dpu !== selectedDpu) {
			if (selectedDpu !== null) {
				highlightDpuLines(selectedDpu, false);
			}
			if (dpu !== null) {
				highlightDpuLines(dpu, true);
			}
			selectedDpu = dpu;
		}
	}

	function highlightDpuLines(dpu, highlight) {
		var stroke = "#555";
		var strokeWidth = 1.5;
		if (highlight) {
			stroke = "#222";
			strokeWidth = 2.5;
		}
		for (lineId in dpu.connectionFrom) {
			var conn = connections[dpu.connectionFrom[lineId]];
			conn.line.setStroke(stroke);
			conn.line.setStrokeWidth(strokeWidth);
			conn.arrowLeft.setStroke(stroke);
			conn.arrowLeft.setStrokeWidth(strokeWidth);
			conn.arrowRight.setStroke(stroke);
			conn.arrowRight.setStrokeWidth(strokeWidth);
		}
		for (lineId in dpu.connectionTo) {
			conn = connections[dpu.connectionTo[lineId]];
			conn.line.setStroke(stroke);
			conn.line.setStrokeWidth(strokeWidth);
			conn.arrowLeft.setStroke(stroke);
			conn.arrowLeft.setStrokeWidth(strokeWidth);
			conn.arrowRight.setStroke(stroke);
			conn.arrowRight.setStrokeWidth(strokeWidth);
		}
		lineLayer.draw();
	}

	function getDpuPosition(id) {
		/*Dpu dpu = dpus[id];
		 var position = dpu.group.getPosition();
		 return [position.x, position.y];*/
	}

	/** 
	 * Gets DPU on given position, currently not used 
	 * 
	 * @param position Position where to look for Dpu
	 **/
	function getDpuByPosition(position) {
		for (dpuId in dpus) {
			var dpu = dpus[dpuId].group;

			var SminX = dpu.getPosition().x;
			var SmaxX = SminX + dpu.children[0].getWidth();
			var SminY = dpu.getPosition().y;
			var SmaxY = SminY + dpu.children[0].getHeight();

			if (position.x >= SminX && position.x <= SmaxX && position.y >= SminY && position.y <= SmaxY) {
				return dpuId;
			}
		}
		return 0;
	}

	function computeLeftArrowPoints(points) {
		var x = points[2] - points[0];
		var y = points[3] - points[1];
		var dist = 5 / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		var leftX = points[2] - dist * x + dist * y;
		var leftY = points[3] - dist * y - dist * x;
		return [leftX, leftY, points[2], points[3]];
	}

	function computeRightArrowPoints(points) {
		var x = points[2] - points[0];
		var y = points[3] - points[1];
		var dist = 5 / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		var leftX = points[2] - dist * x - dist * y;
		var leftY = points[3] - dist * y + dist * x;
		return [leftX, leftY, points[2], points[3]];
	}

	function resizeStage(height, width) {
		//stage.height = height;
		//stage.width = width;
		//backgroundRect.height = height;
		//backgroundRect.width = width;
		//stage.draw();
	}

	function zoomStage(zoom) {
		scale = zoom;
		stage.setScale(zoom);
		stage.setWidth(1600 * zoom);
		stage.setHeight(630 * zoom);
		stage.draw();
	}

	/**
	 * Creates new Text for given dataUnitName.
	 * @param {int} id Id of corresponding edge.
	 * @param {String} dataUnitName Name of DataUnit. 
	 * @param {type} points
	 * @returns {undefined} Text
	 */
	function createDataUnitNameText(id, dataUnitName, points) {
		var width = 100;
		var textPosition = computeTextPosition(points, width);
		var text = new Kinetic.Text({
			x: textPosition[0],
			y: textPosition[1],
			text: dataUnitName,
			fontSize: 10,
			fontFamily: 'Calibri',
			fill: '#555',
			width: width,
			padding: 6,
			align: 'center'
		});

		text.on('click', function(evt) {
			rpcProxy.onDataUnitNameEditRequested(id);
			evt.cancelBubble = true;
		});
		lineLayer.add(text);
		lineLayer.draw();

		return text;
	}

	/**
	 * Computes coordinates for text placement on edge.
	 * @param {type} linePoints Points of edge.
	 * @param {int} width Width of Text.
	 * @returns {undefined} Coordinates for text placement on edge.
	 */
	function computeTextPosition(linePoints, width) {
		var x = linePoints[0] + ((linePoints[2] - linePoints[0]) / 2);
		var y = linePoints[1] + ((linePoints[3] - linePoints[1]) / 2);
		return [x - (width / 2), y];
	}

	function createTooltip(text) {
		// label with left pointer
		var labelLeft = new Kinetic.Label({
			x: 0,
			y: 0,
			opacity: 0.75
		});

		labelLeft.add(new Kinetic.Tag({
			fill: 'black'
//			pointerDirection: 'left',
//			pointerWidth: 12,
//			pointerHeight: 16,
//			lineJoin: 'round'
		}));

		labelLeft.add(new Kinetic.Text({
			text: text,
			fontFamily: 'Calibri',
			fontSize: 10,
			padding: 3,
			fill: 'white'
		}));
		
		labelLeft.setVisible(false);

		return labelLeft;
	}

	/** 
	 * Computes connection points for uniform visual for 2 DPU 
	 * 
	 * @param start start point
	 * @param end end point
	 **/
	function computeConnectionPoints2(start, end) {

		var EminX = end.getPosition().x;
		var EmaxX = EminX + end.children[0].getWidth();
		var EminY = end.getPosition().y;
		var EmaxY = EminY + end.children[0].getHeight();

		return computeConnectionPoints5(start, EminX, EmaxX, EminY, EmaxY);
	}

	/** 
	 * Computes connection points for uniform visual for DPU and point 
	 *
	 * @param start Start position
	 * @param endX End position x coordinate 
	 * @param endY End position y coordinate
	 **/
	function computeConnectionPoints3(start, endX, endY) {
		return computeConnectionPoints5(start, endX, endX, endY, endY);
	}

	/** 
	 * Computes connection points for uniform visual - internal 
	 
	 * @param start Start position
	 * @param EminX End position x min coordinate 
	 * @param EmaxX End position x max coordinate 
	 * @param EminY End position y min coordinate 
	 * @param EmaxY End position y max coordinate 
	 **/
	function computeConnectionPoints5(start, EminX, EmaxX, EminY, EmaxY) {
		var SminX = start.getPosition().x;
		var SmaxX = SminX + start.children[0].getWidth();
		var SminY = start.getPosition().y;
		var SmaxY = SminY + start.children[0].getHeight();

		var startX = 0;
		var startY = SminY;
		var endX = 0;
		var endY = EminY;

		if (SmaxX <= EminX) {
			startX = SmaxX;
			endX = EminX;
		} else if (SminX >= EmaxX) {
			startX = SminX;
			endX = EmaxX;
		} else if (SminX > EminX) {
			startX = SminX + ((EmaxX - SminX) / 2);
			endX = startX;
		} else if (EminX === EmaxX) {
			startX = EminX;
			endX = EminX;
		} else {
			startX = SmaxX - ((SmaxX - EminX) / 2);
			endX = startX;
		}

		if (SmaxY <= EminY) {
			startY = SmaxY;
			endY = EminY;
		} else if (SminY >= EmaxY) {
			startY = SminY;
			endY = EmaxY;
		} else if (SminY > EminY) {
			startY = SminY + ((EmaxY - SminY) / 2);
			endY = startY;
		} else if (EminY === EmaxY) {
			startY = EminY;
			endY = startY;
		} else {
			startY = SmaxY - ((SmaxY - EminY) / 2);
			endY = startY;
		}

		return [startX, startY, endX, endY];
	}

	jQuery(document).ready(function() {
		$("#container").mousemove(function(e) {
			lastPositionX = e.pageX;
			lastPositionY = e.pageY;
		});
	});
};
