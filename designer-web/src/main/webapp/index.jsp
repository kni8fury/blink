<html>
<link rel="stylesheet" type="text/css"
	href="js/resources/css/ext-all.css">
<script type="text/javascript" src="js/ext-all.js"></script>
<script type="text/javascript" src="js/app-all.js"></script>
<body>
	<script>
	   var baseURL = 'http://localhost:8080/desginer-web/services/crud/';
	</script>
	<script type="text/javascript" src="js/app-model.js"></script>
	<script type="text/javascript" src="js/app-store.js"></script>
	<script>
	   
		function createGrid(title, store, columns) {
			  return	Ext.create('Ext.grid.Panel', {
			        title: title,
			        store: store,
			        columns:columns,
			        height: 200,
			        width: 400,
			        name: title,
			        closeable: true,
			        selType: 'rowmodel',
			        plugins: [
			            Ext.create('Ext.grid.plugin.RowEditing', {
			                clicksToEdit: 1
			            })
			        ],
			        dockedItems: [{
			            xtype: 'toolbar',
			            items: [{
			                text: 'Add',
			                iconCls: 'icon-add',
			                handler: function(){
			                    store.insert(0, new EntityAttribute());
			                    rowEditing.startEdit(0, 0);
			                }
			            }, '-', {
			                text: 'Delete',
			                iconCls: 'icon-delete',
			                handler: function(){
			                    var selection = grid.getView().getSelectionModel().getSelection()[0];
			                    if (selection) {
			                        store.remove(selection);
			                    }
			                }
			            }]
			        }]
			    });
	    }
	  		
		function getEntityFormItems() {
	
			return [ { name : 'id', xtype: 'hiddenfield',allowBlank : false, value : 0},
			         { fieldLabel : 'Name', name : 'name', allowBlank : false},
			         { fieldLabel : 'Description', name : 'description', allowBlank : false},
				     Ext.create('Ext.form.ComboBox', {
					    fieldLabel : 'Package',
					    name : 'parentPackage.id',
					    store : packageStore,
					    queryMode : 'local',
					    displayField : 'name',
					    valueField : 'id'
				    } )];
		};

		var ctxMenu = Ext.create('Ext.menu.Menu', {
			items : [ {
				text : 'Add Attribute',
				action : 'addattribute'
			}, {
				text : 'Edit',
				action : 'edit'
			}, {
				text : 'Delete',
				action : 'delete'
			} ]
		});

		var containerCtxMenu = Ext.create('Ext.menu.Menu', {
			items : [ {
				text : 'New Package',
				action : 'newpackage',
				handler : clickHandler
			}, {
				text : 'New Enitty',
				action : 'addentity',
				handler : clickHandler
			}, {
				text : 'Manage Type',
				action : 'managetype',
				handler : clickHandler
			}, {
				text : 'Add Type',
				action : 'addtype',
				handler : clickHandler
			}, {
				text : 'Refresh',
				action : 'refresh'
			} ]
		});

		var treePanel = Ext.create('Ext.tree.Panel', {
			contextMenu : ctxMenu,
			title : 'Entity Explorer',
			width : 200,
			height : 150,
			store : store,
			rootVisible : false,
			region : 'west',
			listeners : {
				itemclick : function(view, rec, item, index, eventObj) {
					if (rec.raw.leaf == true)
						modifyEntityForm(rec.raw.id);
					else
						createPackageForm();
				}
			},
			columns : [ {
				xtype : 'treecolumn',
				header : 'Packages',
				dataIndex : 'text',
				flex : 1
			} ]
		});

		var tabs = Ext.create('Ext.tab.Panel', {
			width : 900,
			height : 400,
			region:'center'
		});
		
		
		var resultsPanel = Ext.create('Ext.panel.Panel', {
		    height: 30,
		    region: 'north'
		});
		
		var win ;
		var minAppWin; 
		function createMiniApp( response ) {
			var miniApp = Ext.JSON.decode(response.responseText);
			
			if( response.status == "200")
			   win.hide();
			
			var items = new Array();
			
			if ( miniApp.db ) {
				items.push({
		            xtype: 'fieldset',
		            columnWidth: 0.5,
		            title: 'DB Config',
		            collapsible: true,
		            defaultType: 'textfield',
		            defaults: {
		                anchor: '100%'
		            },
		            layout: 'anchor',
		            items: [{name:'jdbcDriver',fieldLabel:'Driver Name'},
		                    {name:'jdbcURL',fieldLabel:'JDBC URL:'},
		                    {name:'username',fieldLabel:'Username:'},
		                    {name:'password',fieldLabel:'Password:'}
		                   ] 
		            });
			} 
			
			if( miniApp.persistenceAPI ) {
				items.push({
		            xtype: 'fieldset',
		            columnWidth: 0.5,
		            title: 'Persistence API Config',
		            collapsible: true,
		            defaultType: 'textfield',
		            defaults: {
		                anchor: '100%'
		            },
		            layout: 'anchor',
		            items: [{name:'inheritence',fieldLabel:'Inheritence'},
		                    {name:'idgeneration',fieldLabel:'Id Generation'},
		                    {name:'association',fieldLabel:'Association'}
		                   ] 
		            });
			}
			
			if( miniApp.securityAPI ) {
				
			}
			
			if( miniApp.securityStore) {
				if( miniApp.securityStore == "LDAP" ) {
					
					items.push({
			            xtype: 'fieldset',
			            columnWidth: 0.5,
			            title: 'LDAP Config',
			            collapsible: true,
			            defaultType: 'textfield',
			            defaults: {
			                anchor: '100%'
			            },
			            layout: 'anchor',
			            items: [{name:'userProvider',fieldLabel:'Provider'},
			                    {name:'userFilter',fieldLabel:'Filter'},
			                    {name:'authzIdentity',fieldLabel:'Identity'}
			                   ] 
			            });
				} else if ( miniApp.securityStore =="RDBMS") {
					
				} else if ( miniApp.securityStore =="OPENID") {
					
				} else if (miniApp.securityStore == "FACEBOOK" ) {
					
					items.push({
			            xtype: 'fieldset',
			            columnWidth: 0.5,
			            title: 'FaceBook Config',
			            collapsible: true,
			            defaultType: 'textfield',
			            defaults: {
			                anchor: '100%'
			            },
			            layout: 'anchor',
			            items: [{name:'facebook.clientId',fieldLabel:'ClientId'},
			                    {name:'facebook.clientSecret',fieldLabel:'ClientSecret'}
			                   ] 
			            });
				};
			}
			
			if( miniApp.frontend) {
				
			}
			minAppWin =  Ext.create('widget.window', {
                title: 'App Selection',
                closable: true,
                closeAction: 'hide',
                width: 650,
                height: 550,
                layout: 'border',
                bodyStyle: 'padding: 5px;',
                items: [{
                	xtype: 'form',
                	width : 650,
                    region: 'west',
                    title: 'Small - Single Tier App Config',
                    collapsible: true,
                    floatable: false,
                    layout: 'form',
                    defaultType: 'textfield',
                    items:  items
                }]
			});
			minAppWin.show(viewport,  function () {});
			
		}
		function newApp (item, e) {
			win =  Ext.create('widget.window', {
	                title: 'App Selection',
	                closable: true,
	                closeAction: 'hide',
	                //animateTarget: this,
	                width: 650,
	                height: 550,
	                layout: 'border',
	                bodyStyle: 'padding: 5px;',
	                items: [{
	                	xtype: 'form',
	                    region: 'west',
	                    title: 'Small - Single Tier',
	                    width: 200,
	                    split: true,
	                    collapsible: true,
	                    floatable: false,
	                    layout: 'form',
	                    defaultType: 'textfield',
	                    items: [
	                            {
	                            	name:'name',
	                            	fieldLabel:'App Name'
	                            },
	                            {
	                            	name: 'description',
	                            	fieldLabel: 'Description'
	                            },
	                    	Ext.create('Ext.form.ComboBox', {
	                    		name :'db',
	                    	    fieldLabel: 'DB Type',
	                    	    store: dbTypes,
	                    	    queryMode: 'local',
	                    	    displayField: 'name',
	                    	    valueField: 'abbr',

	                    	}), 
	                    	Ext.create('Ext.form.ComboBox', {
	                    		name: 'persistenceAPI',
	                    	    fieldLabel: 'Persistence API',
	                    	    store: persistenceAPITypes,
	                    	    queryMode: 'local',
	                    	    displayField: 'name',
	                    	    valueField: 'abbr',

	                    	}),
	                        Ext.create('Ext.form.ComboBox', {
	                        	name: 'securityAPI',
	                    	    fieldLabel: 'Security API',
	                    	    store: securityAPITypes,
	                    	    queryMode: 'local',
	                    	    displayField: 'name',
	                    	    valueField: 'abbr',

	                    	}),
	                        Ext.create('Ext.form.ComboBox', {
	                        	    name:'securityStore',
		                    	    fieldLabel: 'Security Store',
		                    	    store: securityStoreTypes,
		                    	    queryMode: 'local',
		                    	    displayField: 'name',
		                    	    valueField: 'abbr',

		                    }),
		                    Ext.create('Ext.form.ComboBox', {
		                    	name:'frontend',
	                    	    fieldLabel: 'Frontend',
	                    	    store: frontendTypes,
	                    	    queryMode: 'local',
	                    	    displayField: 'name',
	                    	    valueField: 'abbr',
	                        })
		                    
	                    	],
	                    buttons: [{
	                        text: 'Reset',
	                        handler: function() {
	                            this.up('form').getForm().reset();
	                        }
	                    }, {
	                        text: 'Create',
	                        formBind: true, 
	                        disabled: true,
	                        handler: function() {
	                            var form = this.up('form').getForm();

	                            if (form.isValid()) {
	                            	ajaxRequest(form, 'app', form.getValues(),createMiniApp);
	                            }
	                        }
	                    }]
	                }, {
	                    region: 'center',
	                    title: 'Medium - 3 Tier',
	                    width: 200,
	                    split: true,
	                    collapsible: true,
	                    floatable: false
	                },{
	                    region: 'east',
	                    title: 'Large - Multi Tier',
	                    width: 200,
	                    split: true,
	                    collapsible: true,
	                    floatable: false
	                }]
	            });
			win.show(this,  function () {});
		}
		var menu = Ext.create('Ext.menu.Menu', {
	        items: [               
	            {text: 'App' , handler: newApp },{text: 'Package'}, {text: 'Entity'},{text: 'Page'},{text: 'Menu'},'-', 
	            {
	                text: 'Config',
	                menu: {        
	                    items: [
	                        '<b class="menu-title">Config Category</b>',
	                        {
	                            text: 'Persistence',
	                            group: 'theme'
	                        }, {
	                            text: 'Event Broadcasting',
	                            checked: false,
	                            group: 'theme'
	                        }, {
	                            text: 'Security',
	                            menu: {
	                            	items: [
	                            	        { text: 'SSO' }, {text: 'Social'} ,{ text: 'Local DB'}, { text: 'Local LDAP'}
	                            	       ]
	                            }
	                        }, {
	                            text: 'Cache',
	                            checked: false,
	                            group: 'theme'
	                        },
	                        {
	                            text: 'Validation',
	                            checked: false,
	                            group: 'theme'
	                        },
	                        {
	                            text: 'Frontend',
	                            checked: false,
	                            group: 'theme'
	                        }
	                    ]
	                }
	           }
	        ]
	    }); 
		
		Ext.onReady(function() {
		    resultsPanel.addDocked({ xtype: 'toolbar',
                 dock: 'bottom',
                 items: [{text: 'File' },{
                     text:'New',
                     iconCls: 'bmenu', 
                     menu: menu  
                 }]
		    });
		});
		
		var viewport = new Ext.Viewport({
			title : 'BorderLayout Demo',
			layout : 'border',
 			items : [ resultsPanel, treePanel, tabs, {
				xtype : 'panel',
				title : 'East Panel',
				region : 'east',
				split : true,
				width : 200
			}, {
				xtype : 'panel',
				title : 'South Panel',
				region : 'south',
				split : true,
				collapsible : true,
				collapseMode : 'mini',
				height : 150
			} ]
		});

		function addToViewPort(panel) {
			tabs.setActiveTab(tabs.add(panel));
		}
		function clickHandler(item, e, eOpts) {
			if (item.action == "newpackage")
				createPackageForm();
			else if (item.action == "managetype")
				manageTypeForm();
			else if (item.action == "addtype")
				addNewType();
			else if (item.action == "addentity")
				addNewEntity();
		};

		function addNewEntity() {
			var entityNewForm = createForm('Add Entity', 'entity/',getEntityFormItems(), submitCreateEntityFunction);

			addToViewPort(entityNewForm);
		}

		function addNewType() {
			var items = [ {
				fieldLabel : 'Name',
				name : 'name',
				allowBlank : false
			}, {
				fieldLabel : 'Description',
				name : 'description',
				allowBlank : false
			}, {
				fieldLabel : 'Class Name',
				name : 'className',
				allowBlank : false
			} ];
			var typeForm = createForm('Add Type', 'type/', items);

			addToViewPort(typeForm);
		}

		function manageTypeForm() {
			var columns = [ {
				text : 'Name',
				dataIndex : 'name'
			}, {
				text : 'Class Name',
				dataIndex : 'className',
				flex : 1
			}, {
				text : 'Description',
				dataIndex : 'description'
			} ];

			var typeGrid = createGrid('Manage Types', typeStore, columns);
			addToViewPort(typeGrid);

		}

		function createPackageForm() {
			var items = [ {
				fieldLabel : 'Name',
				name : 'name',
				allowBlank : false
			}, {
				fieldLabel : 'Description',
				name : 'description',
				allowBlank : false
			} ];
			var packageForm = createForm('Add Package', 'package/', items);
			// viewport.getComponent(1).add(packageForm);

			addToViewPort(packageForm);
		}

		function modifyEntityForm(id) {

			var entityForm = createForm('Modify Entity', 'entity/', getEntityFormItems(),submitCreateEntityFunction);
			
			var columns = [{text : 'Id',dataIndex : 'id',editor: 'textfield'}, 
			               {text : 'Name',dataIndex : 'name',editor: 'textfield'}, 
			               {text : 'Description',dataIndex : 'description',editor: 'textfield'},
			               {text : 'Is Searchable',dataIndex : 'searchable',editor: 'textfield'},
			               {text : 'Type',dataIndex : 'type',editor: new Ext.form.field.ComboBox({
                               typeAhead:true,
                               triggerAction:'all',
                               editable:true,
                               selectOnTab:true,
                               store:typeStore,
                               displayField: 'name',
                               valueField: 'name'
                               
                           })} ];

			Ext.Ajax.request({
				url : baseURL + 'entity/' + id,
				method : 'GET',
				success : function(response) {
					var data = Ext.JSON.decode(response.responseText.trim());
					var form = entityForm.getForm();
					form.findField('id').setValue(data.id);
					form.findField('name').setValue(data.name);
					form.findField('description').setValue(data.description);
				    form.findField('parentPackage.id').setValue(data.parentPackage.id);
					
					var attributeStore = Ext.create('Ext.data.JsonStore', {
				        model: 'EntityAttribute',
				        data: data.entityAttributes
				    });

					var attributeGrid = createGrid('Attributes', attributeStore, columns); 

					entityForm.add(attributeGrid);
					addToViewPort(entityForm);
					
				}
			});
		}

		function createForm(title, resource, items ,submitFunction ) {
			
			return Ext.create('Ext.form.Panel', {
				title : title,
				bodyPadding : 5,
				width : 350,
				layout : 'anchor',
				closable : true,
				waitMsgTarget : true,
				defaults : {
					anchor : '100%'
				},
				defaultType : 'textfield',
				items : items,
				buttons : [ {
					text : 'Reset',
					handler : function() {
						this.up('form').getForm().reset();
					}
				}, {
					text : 'Submit',
					formBind : true,
					disabled : true,
					preSubmit : submitFunction,
					handler : function() {
						var formValues = null;

						var form = this.up('form').getForm();
						if( this.preSubmit != undefined ) {
							formValues  = this.preSubmit();
						}else {
							formValues = form.getValues();
						}
						if (form.isValid()) {
							ajaxRequest(form, resource, formValues);
						}
					} 
				} ]
			});
		}

		function submitCreateEntityFunction() {
			var formPanel = this.up('form');
			var form = formPanel.getForm();
            
			var formValues = form.getValues();
 
			formValues.parentPackage  = {'id': form.findField('parentPackage.id').getValue()  };
			var gridRecords = formPanel.items.items[4].getStore().data.items;
			var gridData = new Array();
			for(var i =0; i < gridRecords.length ; i++)  {
			      gridData.push(gridRecords[i].data);
		    }
			formValues.entityAttributes = gridData;
		    return formValues; 
		}
		
		function ajaxRequest(form, resource , formValues, successHandler) {
           if( successHandler == undefined) {
        	   successHandler = function (response) {
        		   Ext.Msg.alert('Success', response.status);
        	   };
           }
			
			Ext.Ajax.request({
				url : baseURL + resource,
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
				jsonData : formValues,
				success : successHandler,
					
					
			   // function(response) {
					//Ext.Msg.alert('Success', response.status);
					//var jsonData = Ext.util.JSON.decode(response.responseText);
					//alert(jsonData.id);
					//alert(response.status);
				//},
				failure : function(form, action) {
					Ext.Msg.alert('Failed', 'Server Error');
				}
			});
		}

		treePanel.on('itemcontextmenu', function(view, record, item, index, event) {
			ctxMenu.showAt(event.getXY());
			event.stopEvent();
		}, this);

		treePanel.on('containercontextmenu', function(view, event, t) {
			containerCtxMenu.showAt(event.getXY());
			event.stopEvent();
		}, this);
		
		
		
	</script>
</body>
</html>
