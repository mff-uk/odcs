/** Function name according to Vaadin specification **/
cz_cuni_xrg_intlib_frontend_gui_components_pipelinecanvas_PipelineCanvas = function() {

    //remainder from JsLabel
    //used to insert container for kineticJS canvas
    var e = this.getElement();
    //e.innerHTML = "<div id='container' style='border:2px solid;width:1500px;height:800px'></div>";


    //provisional placement of DPU and connection "classes"
    /** Class representing DPU for use on Canvas**/
    function Dpu (id, name, description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.group = null;

        this.connectionFrom = [];
        this.connectionTo = [];

        this.getInfo = function() {
            return this.name + ' ' + this.description;
        };
    }

    /** Class representing Connection between 2 DPUs on Canvas **/
    function Connection(id, from, to, line, cmdDelete) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.line = line;
        this.cmdDelete = cmdDelete;
        this.arrowLeft = null;
        this.arrowRight = null;
    }

    /** RPC proxy for calling server-side methods from client **/
    var rpcProxy = this.getRpcProxy();

    /** DPUs and Connections collections**/
    var dpus = {};
    var connections = {};

    /** Registering RPC for calls from server-side**/
    this.registerRpc({
        init: function() {
            init();
        },
        addDpu: function(dpuId, name, description, x , y) {
            addDpu(dpuId, name, description, x , y);
        },
        addConnection: function(id, dpuFrom, dpuTo) {
            addConnection(id, dpuFrom, dpuTo);
        },
        getDpuPosition: function(id) {
            return getDpuPosition(id);
        }
    });


    //dragVariables
    var isDragging = false;
    var dragId = 0;
    var line = null;


    /** Pipeline states
	 * 	NormalMode - standard mode, DPUs can be dragged
	 *  NewConnectionMode - new connection is being created, line follows mouse
	 *  ...
	 * **/
    //Stage state
    const NewConnectionMode = "new_connection_mode";
    const NormalMode = "normal_mode";

    var stageMode = NormalMode;

    /** New connection related variables **/
    //Adding new connection
    var newConnLine = null;
    var newConnStart = null;


    /** Function for moving connection lines after DPU is dragged **/
    function moveLine(dpuId, x, y) {
        var dpu = dpus[dpuId];
        var dpuGroup = dpu.group;
        for(lineId in dpu.connectionFrom) {
            var conn = connections[dpu.connectionFrom[lineId]];
            var dpuTo = dpus[conn.to].group;
            var newPoints = computeConnectionPoints2(dpuGroup, dpuTo);
            conn.line.setPoints(newPoints);
            conn.arrowLeft.setPoints(computeLeftArrowPoints(newPoints));
            conn.arrowRight.setPoints(computeRightArrowPoints(newPoints));
        }
        for(lineId in dpu.connectionTo) {
            conn = connections[dpu.connectionTo[lineId]];
            var dpuFrom = dpus[conn.from].group;
            newPoints = computeConnectionPoints2(dpuFrom, dpuGroup);
            conn.line.setPoints(newPoints);
            conn.arrowLeft.setPoints(computeLeftArrowPoints(newPoints));
            conn.arrowRight.setPoints(computeRightArrowPoints(newPoints));
        }
    }

    /** Writes message on given message layer **/
    function writeMessage(messageLayer, message) {
        var context = messageLayer.getContext();
        messageLayer.clear();
        context.font = '18pt Calibri';
        context.fillStyle = 'black';
        context.fillText(message, 10, 25);
    }

    /** Kinetic stage and layers accessed from different functions **/
    var stage = null;
    /** Layer with DPUs**/
    var dpuLayer = null;
    /** Connection layer **/
    var lineLayer = null;
    /** Mesage/debug layer **/
    var messageLayer = null;

    /** Init function which builds entire stage for pipeline */
    function init() {

        stage = new Kinetic.Stage({
            container: 'container',
            width: e.clientWidth,
            height: e.clientHeight
        });

        dpuLayer = new Kinetic.Layer();
        lineLayer = new Kinetic.Layer();
        messageLayer = new Kinetic.Layer();

        // MouseMove event on stage
        stage.on('mousemove', function() {
            if(isDragging) {
                // Takes care of repositioning connections on dragged DPU
                var mousePos = stage.getMousePosition();
                var x = mousePos.x;
                var y = mousePos.y;
                writeMessage(messageLayer, 'x: ' + x + ', y: ' + y);
                moveLine(dragId, x, y);
            } else if(stageMode == NewConnectionMode) {
                // Repositioning new connection line
                var mousePosition = stage.getMousePosition();
                newConnLine.setPoints(computeConnectionPoints3(newConnStart.group, mousePosition.x, mousePosition.y));
                lineLayer.draw();
            }
        });

        // Redraws Connection layer after drag
        stage.getDragLayer().afterDraw(function() {
            lineLayer.draw();
        });

        //background layer for detection of mouse move on whole stage
        var backgroundLayer = new Kinetic.Layer();
        var backgroundRect = new Kinetic.Rect({
            x: 0,
            y: 0,
            fill: '#fff',
            width: 1500,
            height: 800
        });

        stage.on('click', function() {
            if(stageMode == NewConnectionMode) {
                // Cancels NewConnectionMode
                newConnLine.destroy();
                newConnLine = null;
                newConnStart = null;
                stageMode = NormalMode;
                lineLayer.draw();
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
    }

    /** Builds DPU object and creates its representations on the stage **/
    function addDpu(id, name, description, posX, posY) {

        var dpu = new Dpu(id, name, description);

        // since this text is inside of a defined area, we can center it using align: 'center'
        // Text for DPU name and description
        var complexText = new Kinetic.Text({
            x: 0,
            y: 0,
            text: name + '\n\n'+ description,
            fontSize: 14,
            fontFamily: 'Calibri',
            fill: '#555',
            width: 200,
            padding: 12,
            align: 'center'
        });

        // Graphical representation of DPU
        var rect = new Kinetic.Rect({
            x: 0,
            y: 0,
            stroke: '#555',
            strokeWidth: 5,
            fill: '#ddd',
            width: 200,
            height: complexText.getHeight(),
            shadowColor: 'black',
            shadowBlur: 10,
            shadowOffset: [10, 10],
            shadowOpacity: 0.2,
            cornerRadius: 10
        });

        // Group containing text and rect
        if(posX < 0) {
            var mousePos = stage.getMousePosition();
            posX = mousePos.x;
            posY = mousePos.y;
        }

        var group = new Kinetic.Group({
            x: posX,
            y: posY,
            rotationDeg: 0,
            draggable: true
        });

        // Action bar on DPU
        var actionBar = new Kinetic.Group({
            x: rect.getWidth() - 32,
            y:0,
            width: 32,
            height: 96,
            visible : false
        });

        // New Connection command
        var cmdConnection = new Kinetic.Rect({
            x: 0,
            y: 0,
            width: 32,
            height: 32,
            stroke: '#f00',
            strokeWidth: 3,
            fill : '#a00'
        });

        cmdConnection.on('click', function(evt) {
            if(stageMode == NormalMode) {
                writeMessage(messageLayer, 'action bar clicked');
                var mousePosition = stage.getMousePosition();
                newConnLine = new Kinetic.Line({
                    points: computeConnectionPoints3(group, mousePosition.x, mousePosition.y),
                    stroke: 'red',
                    strokeWidth: 3
                });
                stageMode = NewConnectionMode;
                newConnStart = dpu;
                writeMessage(messageLayer, 'Clicking on:'+ dpu.name);
                lineLayer.add(newConnLine);
                lineLayer.draw();
                evt.cancelBubble = true;
            }
        });
        actionBar.add(cmdConnection);


        // DPU Detail command
/*        var cmdDetail = new Kinetic.Rect({
            x: 0,
            y: 32,
            width: 32,
            height: 32,
            fill: '#00f'
        });

        cmdDetail.on('click', function(evt) {
            writeMessage(messageLayer, 'Detail requested');
            rpcProxy.onDetailRequested(dpu.id);
            evt.cancelBubble = true;
        });

        actionBar.add(cmdDetail);
*/
        // DPU Remove command
/*        var cmdRemove = new Kinetic.Rect({
            x: 0,
            y: 64,
            width: 32,
            height: 32,
            fill: '#0f0'
        });

        cmdRemove.on('click', function(evt) {
            writeMessage(messageLayer, 'DPU removed');
            removeDpu(dpu);
            rpcProxy.onDpuRemoved(dpu.id);
            evt.cancelBubble = true;
        });

        actionBar.add(cmdRemove);
*/

        group.add(rect);
        group.add(complexText);
        group.add(actionBar);

        // Handling the visibility of actionBar
        group.on('mouseenter', function() {
            if(stageMode == NormalMode) {
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
            var groupPos = group.getPosition();

            if(pos.x < groupPos.x || pos.x > groupPos.x + group.getWidth() || pos.y < groupPos.y || pos.y > groupPos.y + group.getHeight()) {
                //clickRect.setVisible(false);
                writeMessage(messageLayer, 'mouseleft');

            }
        });

        // Registering for drag
        group.on('dragstart', function() {
            writeMessage(messageLayer, 'dragstart');
            isDragging = true;
            dragId = id;
        });
        group.on('dragend', function(evt) {
            writeMessage(messageLayer, 'dragend');
            isDragging = false;
            dragId = 0;
            var endPosition = stage.getMousePosition();
            if(endPosition == null) {
                writeMessage(messageLayer, 'DPU removed - Out of Stage');
                removeDpu(dpu);
                rpcProxy.onDpuRemoved(dpu.id);
                evt.cancelBubble = true;
            }
        });

        // Creating new connection
        group.on('click', function(evt) {
            writeMessage(messageLayer, 'Clicked on Extractor');

            if(stageMode == NewConnectionMode) {
                newConnLine.destroy();
                newConnLine = null;
                stageMode = NormalMode;
                idFrom = newConnStart.id;
                idTo = dpu.id; //getDpuByPosition(stage.getMousePosition());
                if(idTo != 0) {
                    rpcProxy.onConnectionAdded(idFrom, parseInt(idTo));
                }
                newConnStart = null;
            } else if(stageMode == NormalMode) {
                if(evt.ctrlKey) {
                    writeMessage(messageLayer, 'DPU removed - CTRL');
                    removeDpu(dpu);
                    rpcProxy.onDpuRemoved(dpu.id);
                    evt.cancelBubble = true;
                } else if(evt.shiftKey) {
                    writeMessage(messageLayer, 'New Edge - SHIFT');
                    var mousePosition = stage.getMousePosition();
                    newConnLine = new Kinetic.Line({
                        points: computeConnectionPoints3(group, mousePosition.x, mousePosition.y),
                        stroke: 'red',
                        strokeWidth: 3
                    });
                    stageMode = NewConnectionMode;
                    newConnStart = dpu;
                    writeMessage(messageLayer, 'Clicking on:'+ dpu.name);
                    lineLayer.add(newConnLine);
                    lineLayer.draw();
                    evt.cancelBubble = true;
                }
            }
        });

        group.on('dblclick', function() {
            if(stageMode == NormalMode) {
                writeMessage(messageLayer, 'Detail requested');
                rpcProxy.onDetailRequested(dpu.id);
                evt.cancelBubble = true;
            }
        });

        dpu.group = group;
        dpus[id] = dpu;
        dpuLayer.add(group);
        dpuLayer.draw();
    }

    /** Adds connection between 2 given DPUs **/
    function addConnection(id, from, to) {

        var dpuFrom = dpus[from].group;
        var dpuTo = dpus[to].group;

        var linePoints = computeConnectionPoints2(dpuFrom, dpuTo);

        // Graphic representation of connection
        line = new Kinetic.Line({
            points: linePoints,
            stroke: 'red',
            strokeWidth: 3
        });

        line.on('click', function(evt) {
            var del = connections[id].cmdDelete;
            var pos = stage.getMousePosition();
            pos.x = pos.x - 16;
            pos.y = pos.y - 16;
            del.setPosition(pos);
            del.moveToTop();
            del.setVisible(true);
            lineLayer.draw();
        });

        var lineArrowLeft = new Kinetic.Line({
            points: computeLeftArrowPoints(linePoints),
            stroke: 'red',
            strokeWidth: 3
        });

        var lineArrowRight = new Kinetic.Line({
            points: computeRightArrowPoints(linePoints),
            stroke: 'red',
            strokeWidth: 3
        });


        // Delete command for connection
        var cmdDelete = new Kinetic.Rect({
            x: 0,
            y: 0,
            width: 32,
            height: 32,
            fill: '#0f0',
            visible: false
        });

        cmdDelete.on('mouseleave', function(evt) {
            cmdDelete.setVisible(false);
            lineLayer.draw();
        });

        cmdDelete.on('click', function(evt) {
            removeConnection(id);
            evt.cancelBubble = true;
        });

        var con = new Connection(id, from, to, line, cmdDelete);
        con.arrowLeft = lineArrowLeft;
        con.arrowRight = lineArrowRight;
        connections[id] = con;
        dpus[from].connectionFrom.push(id);
        dpus[to].connectionTo.push(id);
        lineLayer.add(lineArrowLeft);
        lineLayer.add(lineArrowRight);
        lineLayer.add(cmdDelete);
        lineLayer.add(line);
        lineLayer.draw();
    }

    /** Removes given connection **/
    function removeConnection(id) {
        var con = connections[id];
        var idx = dpus[con.from].connectionFrom.indexOf(id);
        dpus[con.from].connectionFrom.splice(idx, 1);
        idx = dpus[con.to].connectionTo.indexOf(id);
        dpus[con.to].connectionTo.splice(idx, 1);
        con.line.destroy();
        con.cmdDelete.destroy();
        connections[id] = null;
        lineLayer.draw();
    }

    /** Removes DPU and related connections **/
    function removeDpu(dpu) {
        var count = dpu.connectionFrom.length;
        for(var i = 0; i < count; i++) {
            removeConnection(dpu.connectionFrom[0]);
        }
        count = dpu.connectionTo.length;
        for(var i = 0; i < count; i++) {
            removeConnection(dpu.connectionTo[0]);
        }
        dpu.group.destroy();
        dpus[dpu.id] = null;
        dpuLayer.draw();
    }

    function getDpuPosition(id) {
    /*Dpu dpu = dpus[id];
		var position = dpu.group.getPosition();
		return [position.x, position.y];*/
    }

    /** Gets DPU on given position, currently not used **/
    function getDpuByPosition(position) {
        for(dpuId in dpus) {
            var dpu = dpus[dpuId].group;

            var SminX = dpu.getPosition().x;
            var SmaxX = SminX + dpu.children[0].getWidth();
            var SminY = dpu.getPosition().y;
            var SmaxY = SminY + dpu.children[0].getHeight();

            if(position.x >= SminX && position.x <= SmaxX && position.y >= SminY && position.y <= SmaxY) {
                return dpuId;
            }
        }
        return 0;
    }

    function computeLeftArrowPoints(points) {
        var x = points[2] - points[0];
        var y = points[3] - points[1];
        var dist = 20 / Math.sqrt(Math.pow(x,2) + Math.pow(y, 2));
        var leftX = points[2] - dist * x + dist * y;
        var leftY = points[3] - dist * y - dist * x;
        return [leftX, leftY, points[2], points[3]];
     }

     function computeRightArrowPoints(points) {
        var x = points[2] - points[0];
        var y = points[3] - points[1];
        var dist = 20 / Math.sqrt(Math.pow(x,2) + Math.pow(y, 2));
        var leftX = points[2] - dist * x - dist * y;
        var leftY = points[3] - dist * y + dist * x;
        return [leftX, leftY, points[2], points[3]];
     }

    /** Computes connection points for uniform visual for 2 DPU **/
    function computeConnectionPoints2(start, end) {

        var EminX = end.getPosition().x;
        var EmaxX = EminX + end.children[0].getWidth();
        var EminY = end.getPosition().y;
        var EmaxY = EminY + end.children[0].getHeight();

        return computeConnectionPoints5(start, EminX, EmaxX, EminY, EmaxY);
    }

    /** Computes connection points for uniform visual for DPU and point **/
    function computeConnectionPoints3(start, endX, endY) {
        return computeConnectionPoints5(start, endX, endX, endY, endY);
    }

    /** Computes connection points for uniform visual - internal **/
    function computeConnectionPoints5(start, EminX, EmaxX, EminY, EmaxY) {
        var SminX = start.getPosition().x;
        var SmaxX = SminX + start.children[0].getWidth();
        var SminY = start.getPosition().y;
        var SmaxY = SminY + start.children[0].getHeight();

        var startX = 0;
        var startY = SminY;
        var endX = 0;
        var endY = EminY;

        if(SmaxX <= EminX) {
            startX = SmaxX;
            endX = EminX;
        } else if(SminX >= EmaxX) {
            startX = SminX;
            endX = EmaxX;
        } else if (SminX > EminX) {
            startX = SminX + ((EmaxX - SminX) / 2);
            endX = startX;
        } else if (EminX == EmaxX) {
            startX = EminX;
            endX = EminX;
        } else {
            startX = SmaxX - ((SmaxX - EminX) / 2);
            endX = startX;
        }

        if(SmaxY <= EminY) {
            startY = SmaxY;
            endY = EminY;
        } else if(SminY >= EmaxY) {
            startY = SminY;
            endY = EmaxY;
        } else if (SminY > EminY) {
            startY = SminY + ((EmaxY - SminY) / 2);
            endY = startY;
        } else if (EminY == EmaxY) {
            startY = EminY;
            endY = startY;
        } else {
            startY = SmaxY - ((SmaxY - EminY) / 2);
            endY = startY;
        }

        return [startX, startY, endX, endY];
    }

};