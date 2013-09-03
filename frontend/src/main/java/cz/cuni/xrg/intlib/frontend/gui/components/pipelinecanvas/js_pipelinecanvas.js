/**
 * Javacript part of graph canvas.
 * Function name according to Vaadin specification
 *
 * @author Bogo
 * **/
cz_cuni_xrg_intlib_frontend_gui_components_pipelinecanvas_PipelineCanvas = function() {

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
        this.isInMultiselect = false;

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

    //DoubleClick recognition variables
    var lastClickedDpu = null;
    var lastClickedTime = null;


    //Drag variables
    var isDragging = false;
    var dragId = 0;
    var line = null;


    /** Pipeline states
     * 	NORMAL_MODE - standard mode, DPUs can be dragged
     *      NEW_CONNECTION_MODE - new connection is being created, line follows mouse
     *  ...
     * **/
    // Stage state constants
    var NEW_CONNECTION_MODE = "new_connection_mode";
    var NORMAL_MODE = "normal_mode";
    var MULTISELECT_MODE = "multiselect_mode";

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
    var formatIcon = null;
	var copyIcon = null;
    var distributeIcon = null;

    var backgroundRect = null;

    var tooltip = null;
    var formattingActionBar = null;

    /** Init function which builds entire stage for pipeline */
    function init() {
        if (stage === null) {
            stage = new Kinetic.Stage({
                container: 'container',
                width: 1600,
                height: 630
            });
        }

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
                moveLine(dragId);
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
            } else if (stageMode === MULTISELECT_MODE) {
                cancelMultiselect();
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
        addConnectionIcon.src = basePath + "arrow_right.png";

        removeConnectionIcon = new Image();
        removeConnectionIcon.src = basePath + "TrashFull.png";

        debugIcon = new Image();
        debugIcon.src = basePath + "debug.png";

        detailIcon = new Image();
        detailIcon.src = basePath + "Gear.png";

        formatIcon = new Image();
        formatIcon.src = basePath + "format.png";
		
		copyIcon = new Image();
		copyIcon.src = basePath + "copy.png";

        distributeIcon = new Image();
        distributeIcon.src = basePath + "distribute.png";

        tooltip = createTooltip('Tooltip');
        dpuLayer.add(tooltip);

        formattingActionBar = createFormattingActionBar();
        dpuLayer.add(formattingActionBar);
    }

    /** 
     * Function for moving connection lines after DPU is dragged 
     * @param dpuId id of dpu which was moved
     **/
    function moveLine(dpuId) {
        var dpu = dpus[dpuId];
        var dpuGroup = dpu.group;
        for (lineId in dpu.connectionFrom) {
            var conn = connections[dpu.connectionFrom[lineId]];
            var dpuTo = dpus[conn.to].group;
            var newPoints = computeConnectionPoints2(dpuGroup, dpuTo);
            conn.line.setPoints(newPoints);
            conn.hitLine.setPoints(newPoints);
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
            conn.hitLine.setPoints(newPoints);
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
//
//        rpcProxy.onLogMessage(message);
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
		var stroke = '#555';
		if(dataUnitName === null || dataUnitName === "") {
			stroke = '#F00';
		} 
		con.line.setStroke(stroke);
		con.arrowLeft.setStroke(stroke);
		con.arrowRight.setStroke(stroke);
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
        for (var connId in connections) {
            removeConnection(connId);
        }
        connections = [];
        for (var dpuId in dpus) {
            var dpu = dpus[dpuId];
            if (dpu !== null) {
                removeDpu(dpu);
            }
        }
        dpus = [];
        //init();
    }

    function activateTooltip(text) {
        tooltip.getText().setText(text);
        var position = stage.getMousePosition();
        position.x = (position.x + 16) / scale;
        position.y = position.y / scale;
        tooltip.setPosition(position);
        tooltip.moveToTop();
        tooltip.setVisible(true);
        dpuLayer.draw();
    }

    function deactivateTooltip() {
        tooltip.setVisible(false);
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
            x: rect.getWidth() - 20,
            y: 0,
            width: 20,
            height: 100,
            visible: false
        });
		
		var rectAb = new Kinetic.Rect({
            x: 0,
            y: 0,
            stroke: '#555',
            strokeWidth: 1,
            fill: '#ccc',
            width: 20,
            height: 100,
            shadowColor: 'black',
            shadowBlur: 2,
            shadowOffset: [2, 2],
            shadowOpacity: 0.2,
            cornerRadius: 2
        });
		actionBar.add(rectAb);

        // New Connection command
        var cmdConnection = new Kinetic.Image({
            x: 2,
            y: 2,
            image: addConnectionIcon,
            width: 16,
            height: 16,
            startScale: 1
        });

        cmdConnection.on('click', function(evt) {
			actionBar.setVisible(false);
			dpuLayer.draw();
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
            activateTooltip('Create new edge');
            //evt.cancelBubble = true;
        });
        cmdConnection.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdConnection);

        // DPU Detail command
        var cmdDetail = new Kinetic.Image({
            x: 2,
            y: 18,
            image: detailIcon,
            width: 16,
            height: 16,
            startScale: 1
        });

        cmdDetail.on('click', function(evt) {
			actionBar.setVisible(false);
			dpuLayer.draw();
            writeMessage(messageLayer, 'DPU detail requested');
            rpcProxy.onDetailRequested(dpu.id);
            evt.cancelBubble = true;
        });
        cmdDetail.on('mouseenter', function(evt) {
            activateTooltip('Show detail');
            //evt.cancelBubble = true;
        });
        cmdDetail.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdDetail);

        // DPU Remove command
        var cmdRemove = new Kinetic.Image({
            x: 2,
            y: 82,
            image: removeConnectionIcon,
            width: 16,
            height: 16,
            startScale: 1
        });

        cmdRemove.on('click', function(evt) {
			actionBar.setVisible(false);
			dpuLayer.draw();
            writeMessage(messageLayer, 'DPU removed');
            removeDpu(dpu);
            rpcProxy.onDpuRemoved(dpu.id);
            evt.cancelBubble = true;
        });
        cmdRemove.on('mouseenter', function(evt) {
            activateTooltip('Remove DPU');
            //evt.cancelBubble = true;
        });
        cmdRemove.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdRemove);

        // Debug command
        var cmdDebug = new Kinetic.Image({
            x: 2,
            y: 34,
            image: debugIcon,
            width: 16,
            height: 16,
            startScale: 1
        });

        cmdDebug.on('click', function(evt) {
			actionBar.setVisible(false);
			dpuLayer.draw();
            writeMessage(messageLayer, 'Debug requested');
            rpcProxy.onDebugRequested(dpu.id);
            evt.cancelBubble = true;
        });
        cmdDebug.on('mouseenter', function(evt) {
            activateTooltip('Debug to this DPU');
            //evt.cancelBubble = true;
        });
        cmdDebug.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdDebug);

        var cmdFormat = new Kinetic.Image({
            x: 2,
            y: 66,
            image: formatIcon,
            width: 16,
            height: 16,
            startScale: 1
        });
        cmdFormat.on('click', function(evt) {
			actionBar.setVisible(false);
			dpuLayer.draw();
            writeMessage(messageLayer, 'Format clicked');
            stageMode = MULTISELECT_MODE;
            multiselect(dpu.id);
            evt.cancelBubble = true;
        });
        cmdFormat.on('mouseenter', function(evt) {
            activateTooltip('DPU layout formatting');
        });
        cmdFormat.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdFormat);
		
		var cmdCopy = new Kinetic.Image({
            x: 2,
            y: 50,
            image: copyIcon,
            width: 16,
            height: 16,
            startScale: 1
        });
        cmdCopy.on('click', function(evt) {
			actionBar.setVisible(false);
			dpuLayer.draw();
            writeMessage(messageLayer, 'Copy clicked');
			var mousePosition = stage.getMousePosition();
            rpcProxy.onDpuCopyRequested(dpu.id, parseInt(mousePosition.x / scale), parseInt(mousePosition.y / scale));
            evt.cancelBubble = true;
        });
        cmdCopy.on('mouseenter', function(evt) {
            activateTooltip('Copy DPU');
        });
        cmdCopy.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdCopy);


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
            } else if (stageMode === MULTISELECT_MODE) {
                formattingActionBar.setVisible(true);
                formattingActionBar.moveToTop();
                var pos = stage.getMousePosition();
                pos.x = (pos.x) / scale;
                pos.y = (pos.y) / scale;
                formattingActionBar.setPosition(pos);
                dpuLayer.draw();
            }
        });

        group.on('mouseleave', function() {
            actionBar.setVisible(false);
            if (stageMode === MULTISELECT_MODE) {
                //formattingActionBar.setVisible(false);
            }
            dpuLayer.draw();
            return;
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
					writeMessage(messageLayer, 'Format by CTRL');
					stageMode = MULTISELECT_MODE;
					multiselect(dpu.id);
                    //writeMessage(messageLayer, 'DPU removed - CTRL');
                    //removeDpu(dpu);
                    //rpcProxy.onDpuRemoved(dpu.id);
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
            } else if (stageMode === MULTISELECT_MODE) {
                multiselect(dpu.id);
                evt.cancelBubble = true;
            }
        });

        dpu.rect = rect;
        dpu.text = complexText;
        dpu.group = group;
        dpus[id] = dpu;
        dpuLayer.add(group);
        dpuLayer.draw();

        rpcProxy.onDpuMoved(id, parseInt(posX / scale), parseInt(posY / scale));
    }

    /**
     * Add or removes dpu with given id to/from multiselect.
     * 
     * @param {int} id Id of dpu to add to/remove from multiselect.
     * 
     */
    function multiselect(id) {
        var dpu = dpus[id];
        dpu.isInMultiselect = !dpu.isInMultiselect;
        var rect = dpu.group.get('Rect')[0];
        if (dpu.isInMultiselect) {
            rect.setStrokeWidth(4);
            writeMessage(messageLayer, 'Selecting DPU');
        } else {
            rect.setStrokeWidth(2);
            writeMessage(messageLayer, 'Unselecting DPU');
        }
        dpuLayer.draw();
    }

    /**
     * Cancels multiselect mode and unselects all selected DPUs.
     */
    function cancelMultiselect() {
        writeMessage(messageLayer, 'canceling multiselect');
        for (var dpuId in dpus) {
            var dpu = dpus[dpuId];
            if (dpu.isInMultiselect) {
                dpu.isInMultiselect = false;
                dpu.group.get('Rect')[0].setStrokeWidth(2);
            }
        }
        formattingActionBar.setVisible(false);
        dpuLayer.draw();
        stageMode = NORMAL_MODE;
    }

    /**
     * Creates action bar for formatting actions.
     * 
     * @returns Created action bar.
     */
    function createFormattingActionBar() {
        var actionBar = new Kinetic.Group({
            x: 0,
            y: 0,
            width: 64,
            height: 48,
            visible: false
        });

        // Align left command
        var cmdLeft = new Kinetic.Image({
            x: 0,
            y: 16,
            image: addConnectionIcon,
            width: 16,
            height: 16,
            startScale: 1,
            rotationDeg: 180,
            offset: [8, 8]
        });

        cmdLeft.on('click', function(evt) {
            multiselectAlign('left');
            evt.cancelBubble = true;
        });
        cmdLeft.on('mouseenter', function() {
            activateTooltip('Align left');
        });
        cmdLeft.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdLeft);

        // Align right command
        var cmdRight = new Kinetic.Image({
            x: 32,
            y: 16,
            image: addConnectionIcon,
            width: 16,
            height: 16,
            startScale: 1,
            rotationDeg: 0,
            offset: [8, 8]
        });

        cmdRight.on('click', function(evt) {
            multiselectAlign('right');
            evt.cancelBubble = true;
        });
        cmdRight.on('mouseenter', function() {
            activateTooltip('Align right');
        });
        cmdRight.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdRight);

        // Align top command
        var cmdTop = new Kinetic.Image({
            x: 16,
            y: 0,
            image: addConnectionIcon,
            width: 16,
            height: 16,
            startScale: 1,
            rotationDeg: 270,
            offset: [8, 8]
        });

        cmdTop.on('click', function(evt) {
            multiselectAlign('top');
            evt.cancelBubble = true;
        });
        cmdTop.on('mouseenter', function() {
            activateTooltip('Align top');
        });
        cmdTop.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdTop);

        // Align bottom command
        var cmdBottom = new Kinetic.Image({
            x: 16,
            y: 32,
            image: addConnectionIcon,
            width: 16,
            height: 16,
            startScale: 1,
            rotationDeg: 90,
            offset: [8, 8]
        });

        cmdBottom.on('click', function(evt) {
            multiselectAlign('bottom');
            evt.cancelBubble = true;
        });
        cmdBottom.on('mouseenter', function() {
            activateTooltip('Align bottom');
        });
        cmdBottom.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdBottom);

        // Distribute horizontally command
        var cmdHorizontal = new Kinetic.Image({
            x: 48,
            y: 0,
            image: distributeIcon,
            width: 16,
            height: 16,
            startScale: 1,
            rotationDeg: 0,
            offset: [8, 8]
        });

        cmdHorizontal.on('click', function(evt) {
            multiselectDistribute('horizontal');
            evt.cancelBubble = true;
        });
        cmdHorizontal.on('mouseenter', function() {
            activateTooltip('Distribute horizontally');
        });
        cmdHorizontal.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdHorizontal);

        // Distribute vertically command
        var cmdVertical = new Kinetic.Image({
            x: 48,
            y: 16,
            image: distributeIcon,
            width: 16,
            height: 16,
            startScale: 1,
            rotationDeg: 90,
            offset: [8, 8]
        });

        cmdVertical.on('click', function(evt) {
            multiselectDistribute('vertical');
            evt.cancelBubble = true;
        });
        cmdVertical.on('mouseenter', function() {
            activateTooltip('Distribute vertically');
        });
        cmdVertical.on('mouseleave', function(evt) {
            deactivateTooltip();
            evt.cancelBubble = true;
        });
        actionBar.add(cmdVertical);

        return actionBar;
    }

    /**
     * Alings selected DPUs by given type of align.
     * 
     * @param {type} type Type of align
     */
    function multiselectAlign(type) {
        writeMessage(messageLayer, 'Type: ' + type);
        rpcProxy.onStoreHistory();
        var x;
        switch (type) {
            case 'left':
            case 'top':
                x = 10000;
                break;
            case 'right':
            case 'bottom':
                x = 0;
                break;
            default:
                return;
        }
        //Get the extreme value for needed coordinate
        for (var dpuId in dpus) {
            var dpu = dpus[dpuId];
            if (dpu.isInMultiselect) {
                var group = dpu.group;
                var y;
                //Get the right component of position
                if (type === 'left' || type === 'right') {
                    y = group.getPosition().x;
                } else {
                    y = group.getPosition().y;
                }
                //Use the right compare
                if (type === 'left' || type === 'top') {
                    if (y < x) {
                        x = y;
                    }
                } else {
                    if (y > x) {
                        x = y;
                    }
                }
            }
        }
        //Set new value to the right coordinate
        for (var dpuId in dpus) {
            var dpu = dpus[dpuId];
            if (dpu.isInMultiselect) {
                var group = dpu.group;
                if (type === 'left' || type === 'right') {
                    group.setX(x);
                } else {
                    group.setY(x);
                }
                moveLine(dpu.id);
                rpcProxy.onDpuMoved(dpu.id, parseInt(group.getX()), parseInt(group.getY()));
            }
        }
        dpuLayer.draw();
    }

    /**
     * Distributes selected DPUs by given type of distribution.
     * 
     * @param {String} type Type of distribution
     */
    function multiselectDistribute(type) {
        rpcProxy.onStoreHistory();
        var units = [];
        var min = 10000;
        var max = 0;
        var fill = 0;
        for (var dpuId in dpus) {
            var dpu = dpus[dpuId];
            if (dpu.isInMultiselect) {
                var group = dpu.group;
                var x;
                if (type === "horizontal") {
                    x = group.getX();
                    units.push([x, dpuId]);
                    fill += group.get('Rect')[0].getWidth();
                } else {
                    x = group.getY();
                    units.push([x, dpuId]);
                    fill += group.get('Rect')[0].getHeight();
                }
                if (x > max) {
                    max = x;
                } 
                if (x < min) {
                    min = x;
                }
            }
        }
        if(units.length < 2) {
            return;
        }
        units.sort(function(a, b) {
            return a[0] - b[0];
        });
        var lastUnit = dpus[units[units.length - 1][1]].group.get('Rect')[0];
        var body = type === "horizontal" ? lastUnit.getWidth() : lastUnit.getHeight();
        var step = (max + body - min - fill) / (units.length - 1);
        var newValue = min;
        for (var unitId in units) {
            var dpu = dpus[units[unitId][1]];
            if (type === "horizontal") {
                dpu.group.setX(newValue);
                newValue += dpu.group.get('Rect')[0].getWidth() + step;
            } else {
                dpu.group.setY(newValue);
                newValue += dpu.group.get('Rect')[0].getHeight() + step;
            }
            moveLine(dpu.id);
            rpcProxy.onDpuMoved(dpu.id, parseInt(dpu.group.getX()), parseInt(dpu.group.getY()));
        }
        dpuLayer.draw();
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
		var stroke = '#555';
		if(dataUnitName === null || dataUnitName === "") {
			stroke = '#F00';
		}

        // Graphic representation of connection
        line = new Kinetic.Line({
            points: linePoints,
            stroke: stroke,
            strokeWidth: 1.5
        });

        var hitLine = new Kinetic.Line({
            points: linePoints,
            strokeWidth: 15,
            stroke: stroke,
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
            stroke: stroke,
            strokeWidth: 1
        });

        var lineArrowRight = new Kinetic.Line({
            points: computeRightArrowPoints(linePoints),
            stroke: stroke,
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
            width: 20,
            height: 36,
            visible: false
        });
		var rectAb = new Kinetic.Rect({
            x: 0,
            y: 0,
            stroke: '#555',
            strokeWidth: 1,
            fill: '#ccc',
            width: 20,
            height: 36,
            shadowColor: 'black',
            shadowBlur: 2,
            shadowOffset: [2, 2],
            shadowOpacity: 0.2,
            cornerRadius: 2
        });
		actionBar.add(rectAb);

        var cmdName = new Kinetic.Image({
            x: 2,
            y: 2,
            image: detailIcon,
            width: 16,
            height: 16,
            startScale: 1
        });

        cmdName.on('click', function(evt) {
			actionBar.setVisible(false);
			dpuLayer.draw();
            rpcProxy.onDataUnitNameEditRequested(id);
            evt.cancelBubble = true;
        });
        cmdName.on('mouseenter', function() {
            activateTooltip('Set name of DataUnit.');
        });
        cmdName.on('mouseleave', function(evt) {
            deactivateTooltip();
            //evt.cancelBubble = true;
        });
        actionBar.add(cmdName);

        // Delete command for connection
        var cmdDelete = new Kinetic.Image({
            x: 2,
            y: 18,
            image: removeConnectionIcon,
            width: 16,
            height: 16,
            startScale: 1
        });

        cmdDelete.on('click', function(evt) {
			actionBar.setVisible(false);
			dpuLayer.draw();
            removeConnection(id);
            rpcProxy.onConnectionRemoved(id);
            evt.cancelBubble = true;
        });
        cmdDelete.on('mouseenter', function() {
            activateTooltip('Remove the edge');
        });
        cmdDelete.on('mouseleave', function(evt) {
            deactivateTooltip();
            //evt.cancelBubble = true;
        });
        actionBar.add(cmdDelete);

        actionBar.on('mouseleave', function(evt) {
            actionBar.setVisible(false);
            lineLayer.draw();
			evt.cancelBubble = true;
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
        lineLayer.add(line);
        lineLayer.add(hitLine);
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
        if (con === null) {
            return;
        }
        var idx = dpus[con.from].connectionFrom.indexOf(id);
        dpus[con.from].connectionFrom.splice(idx, 1);
        idx = dpus[con.to].connectionTo.indexOf(id);
        dpus[con.to].connectionTo.splice(idx, 1);
		if(con.dataUnitNameText !== null) {
			con.dataUnitNameText.setVisible(false);
			con.dataUnitNameText = null;
		}
        con.line.destroy();
        con.arrowLeft.destroy();
        con.arrowRight.destroy();
        con.actionBar.destroy();
        con.hitLine.destroy();
        connections[id] = null;
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
            var id = dpu.connectionFrom[0];
            removeConnection(id);
            rpcProxy.onConnectionRemoved(id);
        }
        count = dpu.connectionTo.length;
        for (var i = 0; i < count; i++) {
            var id = dpu.connectionTo[0];
            removeConnection(id);
            rpcProxy.onConnectionRemoved(id);
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
		var strokeRed = "#F00";
        var strokeWidth = 1.5;
        if (highlight) {
            stroke = "#222";
            strokeWidth = 2.5;
        }
        for (lineId in dpu.connectionFrom) {
            var conn = connections[dpu.connectionFrom[lineId]];
			var originalStroke = conn.line.getStroke();
			if(originalStroke !== strokeRed) {
				conn.line.setStroke(stroke);
				conn.arrowLeft.setStroke(stroke);
				conn.arrowRight.setStroke(stroke);
			}
            conn.line.setStrokeWidth(strokeWidth);
            conn.arrowLeft.setStrokeWidth(strokeWidth);
            conn.arrowRight.setStrokeWidth(strokeWidth);
        }
        for (lineId in dpu.connectionTo) {
            conn = connections[dpu.connectionTo[lineId]];
            var originalStroke = conn.line.getStroke();
			if(originalStroke !== strokeRed) {
				conn.line.setStroke(stroke);
				conn.arrowLeft.setStroke(stroke);
				conn.arrowRight.setStroke(stroke);
			}
            conn.line.setStrokeWidth(strokeWidth);
            conn.arrowLeft.setStrokeWidth(strokeWidth);
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
