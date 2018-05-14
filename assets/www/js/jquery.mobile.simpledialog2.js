/*
 * jQuery Mobile Framework : plugin to provide a simple Dialog widget.
 * Copyright (c) JTSage
 * CC 3.0 Attribution.  May be relicensed without permission/notifcation.
 * https://github.com/jtsage/jquery-mobile-simpledialog
 */

(function(a, b) {
	a.widget("mobile.simpledialog2", a.mobile.widget, {
		options: {
			version: "1.0.1-2012061300",
			mode: "blank",
			themeDialog: "a",
			themeInput: false,
			themeButtonDefault: false,
			themeHeader: "a",
			fullScreen: false,
			fullScreenForce: false,
			dialogAllow: false,
			dialogForce: false,
			headerText: false,
			headerClose: false,
			buttonPrompt: false,
			buttonInput: false,
			buttonInputDefault: false,
			buttonPassword: false,
			blankContent: false,
			blankContentAdopt: false,
			resizeListener: true,
			safeNuke: true,
			forceInput: true,
			showModal: true,
			animate: true,
			transition: "pop",
			clickEvent: "click",
			zindex: "500",
			width: "280px",
			left: false,
			top: false,
			callbackOpen: false,
			callbackOpenArgs: [],
			callbackClose: false,
			callbackCloseArgs: []
		},
		_eventHandler: function(f, d) {
			var c = f.data.widget,
				g = f.data.widget.options;
			if (!f.isPropagationStopped()) {
				switch (d.method) {
				case "close":
					c.close();
					break;
				case "html":
					c.updateBlank(d.source);
					break
				}
			}
		},
		_create: function() {
			var c = this,
				f = a.extend(this.options, this.element.jqmData("options")),
				e = new Date(),
				d = a("<div class='ui-simpledialog-container ui-overlay-shadow ui-corner-all ui-simpledialog-hidden " + ((f.animate === true) ? f.transition : "") + " ui-body-" + f.themeDialog + "'></div>");
			if (f.themeButtonDefault === false) {
				f.themeButtonDefault = f.themeDialog
			}
			if (f.themeInput === false) {
				f.themeInput = f.themeDialog
			}
			a.mobile.sdCurrentDialog = c;
			if (typeof a.mobile.sdLastInput !== "undefined") {
				delete a.mobile.sdLastInput
			}
			c.internalID = e.getTime();
			c.displayAnchor = a.mobile.activePage.children(".ui-content").first();
			if (c.displayAnchor.length === 0) {
				c.displayAnchor = a.mobile.activePage
			}
			c.dialogPage = a("<div data-role='dialog' data-theme='" + f.themeDialog + "'><div data-role='header'></div><div data-role='content'></div></div>");
			c.sdAllContent = c.dialogPage.find("[data-role=content]");
			d.appendTo(c.sdAllContent);
			c.sdIntContent = c.sdAllContent.find(".ui-simpledialog-container");
			c.sdIntContent.css("width", f.width);
			if (f.headerText !== false || f.headerClose !== false) {
				c.sdHeader = a('<div style="margin-bottom: 4px;" class="ui-header ui-bar-' + f.themeHeader + '"></div>');
				if (f.headerClose === true) {
					a("<a class='ui-btn-left' rel='close' href='#'>Close</a>").appendTo(c.sdHeader).buttonMarkup({
						theme: f.themeHeader,
						icon: "delete",
						iconpos: "notext",
						corners: true,
						shadow: true
					})
				}
				a('<h1 class="ui-title">' + ((f.headerText !== false) ? f.headerText : "") + "</h1>").appendTo(c.sdHeader);
				c.sdHeader.appendTo(c.sdIntContent)
			}
			if (f.mode === "blank") {
				if (f.blankContent === true) {
					if (f.blankContentAdopt === true) {
						f.blankContent = c.element.children()
					} else {
						f.blankContent = c.element.html()
					}
				}
				a(f.blankContent).appendTo(c.sdIntContent)
			} else {
				if (f.mode === "button") {
					c._makeButtons().appendTo(c.sdIntContent)
				}
			}
			c.sdIntContent.appendTo(c.displayAnchor.parent());
			c.dialogPage.appendTo(a.mobile.pageContainer).page().css("minHeight", "0px").css("zIndex", f.zindex);
			if (f.animate === true) {
				c.dialogPage.addClass(f.transition)
			}
			c.screen = a("<div>", {
				"class": "ui-simpledialog-screen ui-simpledialog-hidden"
			}).css("z-index", (f.zindex - 1)).appendTo(c.displayAnchor.parent()).bind(f.clickEvent, function(g) {
				if (!f.forceInput) {
					c.close()
				}
				g.preventDefault()
			});
			if (f.showModal) {
				c.screen.addClass("ui-simpledialog-screen-modal")
			}
			a(document).bind("simpledialog." + c.internalID, {
				widget: c
			}, function(h, g) {
				c._eventHandler(h, g)
			})
		},
		_makeButtons: function() {
			var d = this,
				g = d.options,
				f = a("<div></div>"),
				e = a("<div class='ui-simpledialog-controls'><input class='ui-simpledialog-input ui-input-text ui-shadow-inset ui-corner-all ui-body-" + g.themeInput + "' type='" + ((g.buttonPassword === true) ? "password" : "text") + "' value='" + ((g.buttonInputDefault !== false) ? g.buttonInputDefault.replace('"', "&#34;").replace("'", "&#39;") : "") + "' name='pickin' /></div>"),
				c = a("<div>", {
					"class": "ui-simpledialog-controls"
				});
			if (g.buttonPrompt !== false) {
				d.buttonPromptText = a("<p class='ui-simpledialog-subtitle'>" + g.buttonPrompt + "</p>").appendTo(f)
			}
			if (g.buttonInput !== false) {
				a.mobile.sdLastInput = "";
				e.appendTo(f);
				e.find("input").bind("change", function() {
					a.mobile.sdLastInput = e.find("input").first().val();
					d.thisInput = e.find("input").first().val()
				})
			}
			c.appendTo(f);
			d.butObj = [];
			a.each(g.buttons, function(h, i) {
				i = a.isFunction(i) ? {
					click: i
				} : i;
				i = a.extend({
					text: h,
					id: h + d.internalID,
					theme: g.themeButtonDefault,
					icon: "check",
					iconpos: "left",
					corners: "true",
					shadow: "true",
					args: [],
					close: true
				}, i);
				d.butObj.push(a("<a href='#'>" + h + "</a>").appendTo(c).attr("id", i.id).buttonMarkup({
					theme: i.theme,
					icon: i.icon,
					iconpos: i.iconpos,
					corners: i.corners,
					shadow: i.shadow
				}).unbind("vclick click").bind(g.clickEvent, function() {
					if (g.buttonInput) {
						d.sdIntContent.find("input [name=pickin]").trigger("change")
					}
					var j = i.click.apply(d, a.merge(arguments, i.args));
					if (j !== false && i.close === true) {
						d.close()
					}
				}))
			});
			return f
		},
		_getCoords: function(i) {
			var d = i,
				g = a.mobile.activePage.width(),
				e = a(window).scrollTop(),
				f = a(window).height(),
				c = i.sdIntContent.innerWidth(),
				j = i.sdIntContent.outerHeight(),
				h = {
					high: a(window).height(),
					width: a.mobile.activePage.width(),
					fullTop: a(window).scrollTop(),
					fullLeft: a(window).scrollLeft(),
					winTop: e + ((i.options.top !== false) ? i.options.top : ((f / 2) - (j / 2))),
					winLeft: ((i.options.left !== false) ? i.options.left : ((g / 2) - (c / 2)))
				};
			if (h.winTop < 45) {
				h.winTop = 45
			}
			return h
		},
		_orientChange: function(f) {
			var c = f.data.widget,
				g = f.data.widget.options,
				d = f.data.widget._getCoords(f.data.widget);
			f.stopPropagation();
			if (c.isDialog === true) {
				return true
			} else {
				if (g.fullScreen === true && (d.width < 400 || g.fullScreenForce === true)) {
					c.sdIntContent.css({
						border: "none",
						position: "absolute",
						top: d.fullTop,
						left: d.fullLeft,
						height: d.high,
						width: d.width,
						maxWidth: d.width
					}).removeClass("ui-simpledialog-hidden")
				} else {
					c.sdIntContent.css({
						position: "absolute",
						top: d.winTop,
						left: d.winLeft
					}).removeClass("ui-simpledialog-hidden")
				}
			}
		},
		repos: function() {
			var c = {
				data: {
					widget: this
				},
				stopPropagation: function() {
					return true
				}
			};
			this._orientChange(c)
		},
		open: function() {
			var c = this,
				e = this.options,
				d = this._getCoords(this);
			c.sdAllContent.find(".ui-btn-active").removeClass("ui-btn-active");
			c.sdIntContent.delegate("[rel=close]", e.clickEvent, function(f) {
				f.preventDefault();
				c.close()
			});
			if ((e.dialogAllow === true && d.width < 400) || e.dialogForce) {
				c.isDialog = true;
				if (e.mode === "blank") {
					c.sdIntContent.find("select").each(function() {
						a(this).jqmData("nativeMenu", true)
					})
				}
				c.displayAnchor.parent().unbind("pagehide.remove");
				c.sdAllContent.append(c.sdIntContent);
				c.sdAllContent.trigger("create");
				if (e.headerText !== false) {
					c.sdHeader.find("h1").appendTo(c.dialogPage.find("[data-role=header]"));
					c.sdIntContent.find(".ui-header").empty().removeClass()
				}
				if (e.headerClose === true) {
					c.dialogPage.find(".ui-header a").bind("click", function() {
						setTimeout("$.mobile.sdCurrentDialog.destroy();", 1000)
					})
				} else {
					c.dialogPage.find(".ui-header a").remove()
				}
				c.sdIntContent.removeClass().css({
					top: "auto",
					width: "auto",
					left: "auto",
					marginLeft: "auto",
					marginRight: "auto",
					zIndex: e.zindex
				});
				a.mobile.changePage(c.dialogPage, {
					transition: (e.animate === true) ? e.transition : "none"
				})
			} else {
				c.isDialog = false;
				c.selects = [];
				if (e.fullScreen === false) {
					if (e.showModal === true && e.animate === true) {
						c.screen.fadeIn("slow")
					} else {
						c.screen.removeClass("ui-simpledialog-hidden")
					}
				}
				c.sdIntContent.addClass("ui-overlay-shadow in").css("zIndex", e.zindex).trigger("create");
				if (e.fullScreen === true && (d.width < 400 || e.fullScreenForce === true)) {
					c.sdIntContent.removeClass("ui-simpledialog-container").css({
						border: "none",
						position: "absolute",
						top: d.fullTop,
						left: d.fullLeft,
						height: d.high,
						width: d.width,
						maxWidth: d.width
					}).removeClass("ui-simpledialog-hidden")
				} else {
					c.sdIntContent.css({
						position: "absolute",
						top: d.winTop,
						left: d.winLeft
					}).removeClass("ui-simpledialog-hidden")
				}
				a(document).bind("orientationchange.simpledialog", {
					widget: c
				}, function(f) {
					c._orientChange(f)
				});
				if (e.resizeListener === true) {
					a(window).bind("resize.simpledialog", {
						widget: c
					}, function(f) {
						c._orientChange(f)
					})
				}
			}
			if (a.isFunction(e.callbackOpen)) {
				e.callbackOpen.apply(c, e.callbackOpenArgs)
			}
		},
		close: function() {
			var c = this,
				e = this.options,
				d;
			if (a.isFunction(c.options.callbackClose)) {
				d = c.options.callbackClose.apply(c, c.options.callbackCloseArgs);
				if (d === false) {
					return false
				}
			}
			if (c.isDialog) {
				a(c.dialogPage).dialog("close");
				c.sdIntContent.addClass("ui-simpledialog-hidden");
				c.sdIntContent.appendTo(c.displayAnchor.parent());
				if (a.mobile.activePage.jqmData("page").options.domCache != true && a.mobile.activePage.is(":jqmData(external-page='true')")) {
					a.mobile.activePage.bind("pagehide.remove", function() {
						a(this).remove()
					})
				}
			} else {
				if (c.options.showModal === true && c.options.animate === true) {
					c.screen.fadeOut("slow")
				} else {
					c.screen.addClass("ui-simpledialog-hidden")
				}
				c.sdIntContent.addClass("ui-simpledialog-hidden").removeClass("in");
				a(document).unbind("orientationchange.simpledialog");
				if (c.options.resizeListener === true) {
					a(window).unbind("resize.simpledialog")
				}
			}
			if (e.mode === "blank" && e.blankContent !== false && e.blankContentAdopt === true) {
				c.element.append(e.blankContent);
				e.blankContent = true
			}
			if (c.isDialog === true || c.options.animate === true) {
				setTimeout(function(f) {
					return function() {
						f.destroy()
					}
				}(c), 1000)
			} else {
				c.destroy()
			}
		},
		destroy: function() {
			var c = this,
				d = c.element;
			if (c.options.mode === "blank") {
				a.mobile.sdCurrentDialog.sdIntContent.find("select").each(function() {
					if (a(this).data("nativeMenu") == false) {
						a(this).data("selectmenu").menuPage.remove();
						a(this).data("selectmenu").screen.remove();
						a(this).data("selectmenu").listbox.remove()
					}
				})
			}
			a(c.sdIntContent).remove();
			a(c.dialogPage).remove();
			a(c.screen).remove();
			a(document).unbind("simpledialog." + c.internalID);
			delete a.mobile.sdCurrentDialog;
			a.Widget.prototype.destroy.call(c);
			if (c.options.safeNuke === true && a(d).parents().length === 0 && a(d).contents().length === 0) {
				d.remove()
			}
		},
		updateBlank: function(e) {
			var c = this,
				d = this.options;
			c.sdIntContent.empty();
			if (d.headerText !== false || d.headerClose !== false) {
				c.sdHeader = a('<div class="ui-header ui-bar-' + d.themeHeader + '"></div>');
				if (d.headerClose === true) {
					a("<a class='ui-btn-left' rel='close' href='#'>Close</a>").appendTo(c.sdHeader).buttonMarkup({
						theme: d.themeHeader,
						icon: "delete",
						iconpos: "notext",
						corners: true,
						shadow: true
					})
				}
				a('<h1 class="ui-title">' + ((d.headerText !== false) ? d.headerText : "") + "</h1>").appendTo(c.sdHeader);
				c.sdHeader.appendTo(c.sdIntContent)
			}
			a(e).appendTo(c.sdIntContent);
			c.sdIntContent.trigger("create");
			a(document).trigger("orientationchange.simpledialog")
		},
		_init: function() {
			this.open()
		}
	})
})(jQuery);