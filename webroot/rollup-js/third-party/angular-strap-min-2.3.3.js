/**
 * angular-strap
 * @version v2.3.3 - 2015-09-24
 * @link http://mgcrea.github.io/angular-strap
 * @author Olivier Louvignes <olivier@mg-crea.com> (https://github.com/mgcrea)
 * @license MIT License, http://www.opensource.org/licenses/MIT
 */
!function (e, t, n) {
	'use strict';
	function a(e, n, a, o, i, r) {
		function s(e, n) {
			return angular.element((n || t).querySelectorAll(e))
		}

		function l(e) {
			return u[e] ? u[e] : u[e] = n.get(e, {cache: r}).then(function (e) {
				return e.data
			})
		}

		this.compile = function (t) {
			t.template && /\.html$/.test(t.template) && (console.warn('Deprecated use of `template` option to pass a file. Please use the `templateUrl` option instead.'), t.templateUrl = t.template, t.template = '');
			var n = t.templateUrl, r = t.template || '', u = t.controller, c = t.controllerAs, d = angular.copy(t.resolve || {}), f = angular.copy(t.locals || {}), p = t.transformTemplate || angular.identity, g = t.bindToController;
			return angular.forEach(d, function (e, t) {
				angular.isString(e) ? d[t] = a.get(e) : d[t] = a.invoke(e)
			}), angular.extend(d, f), n ? d.$template = l(n) : d.$template = e.when(r), t.contentTemplate && (d.$template = e.all([d.$template, l(t.contentTemplate)]).then(function (e) {
				var n = angular.element(e[0]), a = s('[ng-bind="content"]', n[0]).removeAttr('ng-bind').html(e[1]);
				return t.templateUrl || a.next().remove(), n[0].outerHTML
			})), e.all(d).then(function (e) {
				var n = p(e.$template);
				t.html && (n = n.replace(/ng-bind="/gi, 'ng-bind-html="'));
				var a = angular.element('<div>').html(n.trim()).contents(), r = o(a);
				return {
					locals: e, element: a, link: function (t) {
						if (e.$scope = t, u) {
							var n = i(u, e, !0);
							g && angular.extend(n.instance, e);
							var o = angular.isObject(n) ? n : n();
							a.data('$ngControllerController', o), a.children().data('$ngControllerController', o), c && (t[c] = o)
						}
						return r.apply(null, arguments)
					}
				}
			})
		};
		var u = {}
	}

	angular.module('mgcrea.ngStrap.typeahead', ['mgcrea.ngStrap.tooltip', 'mgcrea.ngStrap.helpers.parseOptions']).provider('$typeahead', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			prefixClass: 'typeahead',
			prefixEvent: '$typeahead',
			placement: 'bottom-left',
			templateUrl: 'typeahead/typeahead.tpl.html',
			trigger: 'focus',
			container: !1,
			keyboard: !0,
			html: !1,
			delay: 0,
			minLength: 1,
			filter: 'bsAsyncFilter',
			limit: 6,
			autoSelect: !1,
			comparator: '',
			trimValue: !0
		};
		this.$get = ['$window', '$rootScope', '$tooltip', '$$rAF', '$timeout', function (t, n, a, o, i) {
			function r(t, n, r) {
				var l = {}, u = angular.extend({}, e, r);
				l = a(t, u);
				var c = r.scope, d = l.$scope;
				d.$resetMatches = function () {
					d.$matches = [], d.$activeIndex = u.autoSelect ? 0 : -1
				}, d.$resetMatches(), d.$activate = function (e) {
					d.$$postDigest(function () {
						l.activate(e)
					})
				}, d.$select = function (e, t) {
					d.$$postDigest(function () {
						l.select(e)
					})
				}, d.$isVisible = function () {
					return l.$isVisible()
				}, l.update = function (e) {
					d.$matches = e, d.$activeIndex >= e.length && (d.$activeIndex = u.autoSelect ? 0 : -1), s(d), o(l.$applyPlacement)
				}, l.activate = function (e) {
					d.$activeIndex = e
				}, l.select = function (e) {
					if (-1 !== e) {
						var t = d.$matches[e].value;
						n.$setViewValue(t), n.$render(), d.$resetMatches(), c && c.$digest(), d.$emit(u.prefixEvent + '.select', t, e, l)
					}
				}, l.$isVisible = function () {
					return u.minLength && n ? d.$matches.length && angular.isString(n.$viewValue) && n.$viewValue.length >= u.minLength : !!d.$matches.length
				}, l.$getIndex = function (e) {
					var t = d.$matches.length, n = t;
					if (t) {
						for (n = t; n-- && d.$matches[n].value !== e;);
						if (!(0 > n))return n
					}
				}, l.$onMouseDown = function (e) {
					e.preventDefault(), e.stopPropagation()
				}, l.$onKeyDown = function (e) {
					/(38|40|13)/.test(e.keyCode) && (!l.$isVisible() || 13 === e.keyCode && -1 === d.$activeIndex || (e.preventDefault(), e.stopPropagation()), 13 === e.keyCode && d.$matches.length ? l.select(d.$activeIndex) : 38 === e.keyCode && d.$activeIndex > 0 ? d.$activeIndex-- : 40 === e.keyCode && d.$activeIndex < d.$matches.length - 1 ? d.$activeIndex++ : angular.isUndefined(d.$activeIndex) && (d.$activeIndex = 0), d.$digest())
				};
				var f = l.show;
				l.show = function () {
					f(), i(function () {
						l.$element && l.$element.on('mousedown', l.$onMouseDown), u.keyboard && t && t.on('keydown', l.$onKeyDown)
					}, 0, !1)
				};
				var p = l.hide;
				return l.hide = function () {
					l.$element && l.$element.off('mousedown', l.$onMouseDown), u.keyboard && t && t.off('keydown', l.$onKeyDown), u.autoSelect || l.activate(-1), p()
				}, l
			}

			function s(e) {
				e.$$phase || e.$root && e.$root.$$phase || e.$digest()
			}

			angular.element(t.document.body);
			return r.defaults = e, r
		}]
	}).filter('bsAsyncFilter', ['$filter', function (e) {
		return function (t, n, a) {
			return t && angular.isFunction(t.then) ? t.then(function (t) {
				return e('filter')(t, n, a)
			}) : e('filter')(t, n, a)
		}
	}]).directive('bsTypeahead', ['$window', '$parse', '$q', '$typeahead', '$parseOptions', function (e, t, n, a, o) {
		var i = a.defaults;
		return {
			restrict: 'EAC',
			require: 'ngModel',
			link: function (e, t, n, r) {
				var s = {scope: e};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'placement', 'container', 'delay', 'trigger', 'keyboard', 'html', 'animation', 'filter', 'limit', 'minLength', 'watchOptions', 'selectMode', 'autoSelect', 'comparator', 'id', 'prefixEvent', 'prefixClass'], function (e) {
					angular.isDefined(n[e]) && (s[e] = n[e])
				});
				var l = /^(false|0|)$/i;
				angular.forEach(['html', 'container', 'trimValue'], function (e) {
					angular.isDefined(n[e]) && l.test(n[e]) && (s[e] = !1)
				}), t.attr('autocomplete') || t.attr('autocomplete', 'off');
				var u = s.filter || i.filter, c = s.limit || i.limit, d = s.comparator || i.comparator, f = n.bsOptions;
				u && (f += ' | ' + u + ':$viewValue'), d && (f += ':' + d), c && (f += ' | limitTo:' + c);
				var p = o(f), g = a(t, r, s);
				if (s.watchOptions) {
					var m = p.$match[7].replace(/\|.+/, '').replace(/\(.*\)/g, '').trim();
					e.$watchCollection(m, function (t, n) {
						p.valuesFn(e, r).then(function (e) {
							g.update(e), r.$render()
						})
					})
				}
				e.$watch(n.ngModel, function (t, n) {
					e.$modelValue = t, p.valuesFn(e, r).then(function (e) {
						if (s.selectMode && !e.length && t.length > 0)return void r.$setViewValue(r.$viewValue.substring(0, r.$viewValue.length - 1));
						e.length > c && (e = e.slice(0, c));
						var n = g.$isVisible();
						n && g.update(e), (1 !== e.length || e[0].value !== t) && (!n && g.update(e), r.$render())
					})
				}), r.$formatters.push(function (e) {
					var t = p.displayValue(e);
					return t ? t : e && 'object' != typeof e ? e : ''
				}), r.$render = function () {
					if (r.$isEmpty(r.$viewValue))return t.val('');
					var e = g.$getIndex(r.$modelValue), n = angular.isDefined(e) ? g.$scope.$matches[e].label : r.$viewValue;
					n = angular.isObject(n) ? p.displayValue(n) : n;
					var a = n ? n.toString().replace(/<(?:.|\n)*?>/gm, '') : '';
					t.val(s.trimValue === !1 ? a : a.trim())
				}, e.$on('$destroy', function () {
					g && g.destroy(), s = null, g = null
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.tooltip', ['mgcrea.ngStrap.core', 'mgcrea.ngStrap.helpers.dimensions']).provider('$tooltip', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			customClass: '',
			prefixClass: 'tooltip',
			prefixEvent: 'tooltip',
			container: !1,
			target: !1,
			placement: 'top',
			templateUrl: 'tooltip/tooltip.tpl.html',
			template: '',
			contentTemplate: !1,
			trigger: 'hover focus',
			keyboard: !1,
			html: !1,
			show: !1,
			title: '',
			type: '',
			delay: 0,
			autoClose: !1,
			bsEnabled: !0,
			viewport: {selector: 'body', padding: 0}
		};
		this.$get = ['$window', '$rootScope', '$bsCompiler', '$q', '$templateCache', '$http', '$animate', '$sce', 'dimensions', '$$rAF', '$timeout', function (n, a, o, i, r, s, l, u, c, d, f) {
			function p(i, r) {
				function s() {
					I.$emit(V.prefixEvent + '.show', F)
				}

				function p() {
					if (I.$emit(V.prefixEvent + '.hide', F), R === j) {
						if (z && 'focus' === V.trigger)return i[0].blur();
						A()
					}
				}

				function v() {
					var e = V.trigger.split(' ');
					angular.forEach(e, function (e) {
						'click' === e ? i.on('click', F.toggle) : 'manual' !== e && (i.on('hover' === e ? 'mouseenter' : 'focus', F.enter), i.on('hover' === e ? 'mouseleave' : 'blur', F.leave), 'button' === N && 'hover' !== e && i.on($ ? 'touchstart' : 'mousedown', F.$onFocusElementMouseDown))
					})
				}

				function w() {
					for (var e = V.trigger.split(' '), t = e.length; t--;) {
						var n = e[t];
						'click' === n ? i.off('click', F.toggle) : 'manual' !== n && (i.off('hover' === n ? 'mouseenter' : 'focus', F.enter), i.off('hover' === n ? 'mouseleave' : 'blur', F.leave), 'button' === N && 'hover' !== n && i.off($ ? 'touchstart' : 'mousedown', F.$onFocusElementMouseDown))
					}
				}

				function y() {
					'focus' !== V.trigger ? R.on('keyup', F.$onKeyUp) : i.on('keyup', F.$onFocusKeyUp)
				}

				function b() {
					'focus' !== V.trigger ? R.off('keyup', F.$onKeyUp) : i.off('keyup', F.$onFocusKeyUp)
				}

				function D() {
					f(function () {
						R.on('click', S), h.on('click', F.hide), K = !0
					}, 0, !1)
				}

				function k() {
					K && (R.off('click', S), h.off('click', F.hide), K = !1)
				}

				function S(e) {
					e.stopPropagation()
				}

				function x(e) {
					e = e || V.target || i;
					var a = e[0], o = 'BODY' === a.tagName, r = a.getBoundingClientRect(), s = {};
					for (var l in r)s[l] = r[l];
					null === s.width && (s = angular.extend({}, s, {
						width: r.right - r.left,
						height: r.bottom - r.top
					}));
					var u = o ? {
						top: 0,
						left: 0
					} : c.offset(a), d = {scroll: o ? t.documentElement.scrollTop || t.body.scrollTop : e.prop('scrollTop') || 0}, f = o ? {
						width: t.documentElement.clientWidth,
						height: n.innerHeight
					} : null;
					return angular.extend({}, s, d, f, u)
				}

				function T(e, t, n, a) {
					var o, i = e.split('-');
					switch (i[0]) {
						case'right':
							o = {
								top: t.top + t.height / 2 - a / 2,
								left: t.left + t.width
							};
							break;
						case'bottom':
							o = {
								top: t.top + t.height,
								left: t.left + t.width / 2 - n / 2
							};
							break;
						case'left':
							o = {
								top: t.top + t.height / 2 - a / 2,
								left: t.left - n
							};
							break;
						default:
							o = {
								top: t.top - a,
								left: t.left + t.width / 2 - n / 2
							}
					}
					if (!i[1])return o;
					if ('top' === i[0] || 'bottom' === i[0])switch (i[1]) {
						case'left':
							o.left = t.left;
							break;
						case'right':
							o.left = t.left + t.width - n
					} else if ('left' === i[0] || 'right' === i[0])switch (i[1]) {
						case'top':
							o.top = t.top - a;
							break;
						case'bottom':
							o.top = t.top + t.height
					}
					return o
				}

				function C(e, t) {
					var n = R[0], a = n.offsetWidth, o = n.offsetHeight, i = parseInt(c.css(n, 'margin-top'), 10), r = parseInt(c.css(n, 'margin-left'), 10);
					isNaN(i) && (i = 0), isNaN(r) && (r = 0), e.top = e.top + i, e.left = e.left + r, c.setOffset(n, angular.extend({
						using: function (e) {
							R.css({
								top: Math.round(e.top) + 'px',
								left: Math.round(e.left) + 'px',
								right: ''
							})
						}
					}, e), 0);
					var s = n.offsetWidth, l = n.offsetHeight;
					if ('top' === t && l !== o && (e.top = e.top + o - l), !/top-left|top-right|bottom-left|bottom-right/.test(t)) {
						var u = E(t, e, s, l);
						if (u.left ? e.left += u.left : e.top += u.top, c.setOffset(n, e), /top|right|bottom|left/.test(t)) {
							var d = /top|bottom/.test(t), f = d ? 2 * u.left - a + s : 2 * u.top - o + l, p = d ? 'offsetWidth' : 'offsetHeight';
							M(f, n[p], d)
						}
					}
				}

				function E(e, t, n, a) {
					var o = {top: 0, left: 0};
					if (!F.$viewport)return o;
					var i = V.viewport && V.viewport.padding || 0, r = x(F.$viewport);
					if (/right|left/.test(e)) {
						var s = t.top - i - r.scroll, l = t.top + i - r.scroll + a;
						s < r.top ? o.top = r.top - s : l > r.top + r.height && (o.top = r.top + r.height - l)
					} else {
						var u = t.left - i, c = t.left + i + n;
						u < r.left ? o.left = r.left - u : c > r.right && (o.left = r.left + r.width - c)
					}
					return o
				}

				function M(e, t, n) {
					var a = m('.tooltip-arrow, .arrow', R[0]);
					a.css(n ? 'left' : 'top', 50 * (1 - e / t) + '%').css(n ? 'top' : 'left', '')
				}

				function A() {
					clearTimeout(H), F.$isShown && null !== R && (V.autoClose && k(), V.keyboard && b()), q && (q.$destroy(), q = null), R && (R.remove(), R = F.$element = null)
				}

				var F = {}, V = F.$options = angular.extend({}, e, r), O = F.$promise = o.compile(V), I = F.$scope = V.scope && V.scope.$new() || a.$new(), N = i[0].nodeName.toLowerCase();
				if (V.delay && angular.isString(V.delay)) {
					var P = V.delay.split(',').map(parseFloat);
					V.delay = P.length > 1 ? {show: P[0], hide: P[1]} : P[0]
				}
				F.$id = V.id || i.attr('id') || '', V.title && (I.title = u.trustAsHtml(V.title)), I.$setEnabled = function (e) {
					I.$$postDigest(function () {
						F.setEnabled(e)
					})
				}, I.$hide = function () {
					I.$$postDigest(function () {
						F.hide()
					})
				}, I.$show = function () {
					I.$$postDigest(function () {
						F.show()
					})
				}, I.$toggle = function () {
					I.$$postDigest(function () {
						F.toggle()
					})
				}, F.$isShown = I.$isShown = !1;
				var H, L, U, R, Y, q;
				O.then(function (e) {
					U = e, F.init()
				}), F.init = function () {
					V.delay && angular.isNumber(V.delay) && (V.delay = {
						show: V.delay,
						hide: V.delay
					}), 'self' === V.container ? Y = i : angular.isElement(V.container) ? Y = V.container : V.container && (Y = m(V.container)), v(), V.target && (V.target = angular.isElement(V.target) ? V.target : m(V.target)), V.show && I.$$postDigest(function () {
						'focus' === V.trigger ? i[0].focus() : F.show()
					})
				}, F.destroy = function () {
					w(), A(), I.$destroy()
				}, F.enter = function () {
					return clearTimeout(H), L = 'in', V.delay && V.delay.show ? void(H = setTimeout(function () {
						'in' === L && F.show()
					}, V.delay.show)) : F.show()
				}, F.show = function () {
					if (V.bsEnabled && !F.$isShown) {
						I.$emit(V.prefixEvent + '.show.before', F);
						var e, t;
						V.container ? (e = Y, t = Y[0].lastChild ? angular.element(Y[0].lastChild) : null) : (e = null, t = i), R && A(), q = F.$scope.$new(), R = F.$element = U.link(q, function (e, t) {
						}), R.css({
							top: '-9999px',
							left: '-9999px',
							right: 'auto',
							display: 'block',
							visibility: 'hidden'
						}), V.animation && R.addClass(V.animation), V.type && R.addClass(V.prefixClass + '-' + V.type), V.customClass && R.addClass(V.customClass), t ? t.after(R) : e.prepend(R), F.$isShown = I.$isShown = !0, g(I), F.$applyPlacement(), angular.version.minor <= 2 ? l.enter(R, e, t, s) : l.enter(R, e, t).then(s), g(I), d(function () {
							R && R.css({visibility: 'visible'})
						}), V.keyboard && ('focus' !== V.trigger && F.focus(), y()), V.autoClose && D()
					}
				}, F.leave = function () {
					return clearTimeout(H), L = 'out', V.delay && V.delay.hide ? void(H = setTimeout(function () {
						'out' === L && F.hide()
					}, V.delay.hide)) : F.hide()
				};
				var z, j;
				F.hide = function (e) {
					F.$isShown && (I.$emit(V.prefixEvent + '.hide.before', F), z = e, j = R, angular.version.minor <= 2 ? l.leave(R, p) : l.leave(R).then(p), F.$isShown = I.$isShown = !1, g(I), V.keyboard && null !== R && b(), V.autoClose && null !== R && k())
				}, F.toggle = function () {
					F.$isShown ? F.leave() : F.enter()
				}, F.focus = function () {
					R[0].focus()
				}, F.setEnabled = function (e) {
					V.bsEnabled = e
				}, F.setViewport = function (e) {
					V.viewport = e
				}, F.$applyPlacement = function () {
					if (R) {
						var t = V.placement, n = /\s?auto?\s?/i, a = n.test(t);
						a && (t = t.replace(n, '') || e.placement), R.addClass(V.placement);
						var o = x(), i = R.prop('offsetWidth'), r = R.prop('offsetHeight');
						if (F.$viewport = V.viewport && m(V.viewport.selector || V.viewport), a) {
							var s = t, l = x(F.$viewport);
							s.indexOf('bottom') >= 0 && o.bottom + r > l.bottom ? t = s.replace('bottom', 'top') : s.indexOf('top') >= 0 && o.top - r < l.top && (t = s.replace('top', 'bottom')), ('right' === s || 'bottom-left' === s || 'top-left' === s) && o.right + i > l.width ? t = 'right' === s ? 'left' : t.replace('left', 'right') : ('left' === s || 'bottom-right' === s || 'top-right' === s) && o.left - i < l.left && (t = 'left' === s ? 'right' : t.replace('right', 'left')), R.removeClass(s).addClass(t)
						}
						var u = T(t, o, i, r);
						C(u, t)
					}
				}, F.$onKeyUp = function (e) {
					27 === e.which && F.$isShown && (F.hide(), e.stopPropagation())
				}, F.$onFocusKeyUp = function (e) {
					27 === e.which && (i[0].blur(), e.stopPropagation())
				}, F.$onFocusElementMouseDown = function (e) {
					e.preventDefault(), e.stopPropagation(), F.$isShown ? i[0].blur() : i[0].focus()
				};
				var K = !1;
				return F
			}

			function g(e) {
				e.$$phase || e.$root && e.$root.$$phase || e.$digest()
			}

			function m(e, n) {
				return angular.element((n || t).querySelectorAll(e))
			}

			var $ = (String.prototype.trim, 'createTouch'in n.document), h = angular.element(n.document);
			return p
		}]
	}).directive('bsTooltip', ['$window', '$location', '$sce', '$tooltip', '$$rAF', function (e, t, n, a, o) {
		return {
			restrict: 'EAC', scope: !0, link: function (e, t, i, r) {
				var s = {scope: e};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'contentTemplate', 'placement', 'container', 'delay', 'trigger', 'html', 'animation', 'backdropAnimation', 'type', 'customClass', 'id'], function (e) {
					angular.isDefined(i[e]) && (s[e] = i[e])
				});
				var l = /^(false|0|)$/i;
				angular.forEach(['html', 'container'], function (e) {
					angular.isDefined(i[e]) && l.test(i[e]) && (s[e] = !1)
				});
				var u = t.attr('data-target');
				angular.isDefined(u) && (l.test(u) ? s.target = !1 : s.target = u), e.hasOwnProperty('title') || (e.title = ''), i.$observe('title', function (t) {
					if (angular.isDefined(t) || !e.hasOwnProperty('title')) {
						var a = e.title;
						e.title = n.trustAsHtml(t), angular.isDefined(a) && o(function () {
							c && c.$applyPlacement()
						})
					}
				}), i.bsTooltip && e.$watch(i.bsTooltip, function (t, n) {
					angular.isObject(t) ? angular.extend(e, t) : e.title = t, angular.isDefined(n) && o(function () {
						c && c.$applyPlacement()
					})
				}, !0), i.bsShow && e.$watch(i.bsShow, function (e, t) {
					c && angular.isDefined(e) && (angular.isString(e) && (e = !!e.match(/true|,?(tooltip),?/i)), e === !0 ? c.show() : c.hide())
				}), i.bsEnabled && e.$watch(i.bsEnabled, function (e, t) {
					c && angular.isDefined(e) && (angular.isString(e) && (e = !!e.match(/true|1|,?(tooltip),?/i)), e === !1 ? c.setEnabled(!1) : c.setEnabled(!0))
				}), i.viewport && e.$watch(i.viewport, function (e) {
					c && angular.isDefined(e) && c.setViewport(e)
				});
				var c = a(t, s);
				e.$on('$destroy', function () {
					c && c.destroy(), s = null, c = null
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.timepicker', ['mgcrea.ngStrap.helpers.dateParser', 'mgcrea.ngStrap.helpers.dateFormatter', 'mgcrea.ngStrap.tooltip']).provider('$timepicker', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			prefixClass: 'timepicker',
			placement: 'bottom-left',
			templateUrl: 'timepicker/timepicker.tpl.html',
			trigger: 'focus',
			container: !1,
			keyboard: !0,
			html: !1,
			delay: 0,
			useNative: !0,
			timeType: 'date',
			timeFormat: 'shortTime',
			timezone: null,
			modelTimeFormat: null,
			autoclose: !1,
			minTime: -(1 / 0),
			maxTime: +(1 / 0),
			length: 5,
			hourStep: 1,
			minuteStep: 5,
			secondStep: 5,
			roundDisplay: !1,
			iconUp: 'glyphicon glyphicon-chevron-up',
			iconDown: 'glyphicon glyphicon-chevron-down',
			arrowBehavior: 'pager'
		};
		this.$get = ['$window', '$document', '$rootScope', '$sce', '$dateFormatter', '$tooltip', '$timeout', function (t, n, a, o, i, r, s) {
			function l(t, n, a) {
				function o(e) {
					var t = 6e4 * g.minuteStep;
					return new Date(Math.floor(e.getTime() / t) * t)
				}

				function l(e, n) {
					var a = e + n;
					if (t[0].createTextRange) {
						var o = t[0].createTextRange();
						o.collapse(!0), o.moveStart('character', e), o.moveEnd('character', a), o.select()
					} else t[0].setSelectionRange ? t[0].setSelectionRange(e, a) : angular.isUndefined(t[0].selectionStart) && (t[0].selectionStart = e, t[0].selectionEnd = a)
				}

				function d() {
					t[0].focus()
				}

				var f = r(t, angular.extend({}, e, a)), p = a.scope, g = f.$options, m = f.$scope, $ = g.lang, h = function (e, t, n) {
					return i.formatDate(e, t, $, n)
				}, v = 0, w = g.roundDisplay ? o(new Date) : new Date, y = n.$dateValue || w, b = {
					hour: y.getHours(),
					meridian: y.getHours() < 12,
					minute: y.getMinutes(),
					second: y.getSeconds(),
					millisecond: y.getMilliseconds()
				}, D = i.getDatetimeFormat(g.timeFormat, $), k = i.hoursFormat(D), S = i.timeSeparator(D), x = i.minutesFormat(D), T = i.secondsFormat(D), C = i.showSeconds(D), E = i.showAM(D);
				m.$iconUp = g.iconUp, m.$iconDown = g.iconDown, m.$select = function (e, t) {
					f.select(e, t)
				}, m.$moveIndex = function (e, t) {
					f.$moveIndex(e, t)
				}, m.$switchMeridian = function (e) {
					f.switchMeridian(e)
				}, f.update = function (e) {
					angular.isDate(e) && !isNaN(e.getTime()) ? (f.$date = e, angular.extend(b, {
						hour: e.getHours(),
						minute: e.getMinutes(),
						second: e.getSeconds(),
						millisecond: e.getMilliseconds()
					}), f.$build()) : f.$isBuilt || f.$build()
				}, f.select = function (e, t, a) {
					(!n.$dateValue || isNaN(n.$dateValue.getTime())) && (n.$dateValue = new Date(1970, 0, 1)), angular.isDate(e) || (e = new Date(e)), 0 === t ? n.$dateValue.setHours(e.getHours()) : 1 === t ? n.$dateValue.setMinutes(e.getMinutes()) : 2 === t && n.$dateValue.setSeconds(e.getSeconds()), n.$setViewValue(angular.copy(n.$dateValue)), n.$render(), g.autoclose && !a && s(function () {
						f.hide(!0)
					})
				}, f.switchMeridian = function (e) {
					if (n.$dateValue && !isNaN(n.$dateValue.getTime())) {
						var t = (e || n.$dateValue).getHours();
						n.$dateValue.setHours(12 > t ? t + 12 : t - 12), n.$setViewValue(angular.copy(n.$dateValue)), n.$render()
					}
				}, f.$build = function () {
					var e, t, n = m.midIndex = parseInt(g.length / 2, 10), a = [];
					for (e = 0; e < g.length; e++)t = new Date(1970, 0, 1, b.hour - (n - e) * g.hourStep), a.push({
						date: t,
						label: h(t, k),
						selected: f.$date && f.$isSelected(t, 0),
						disabled: f.$isDisabled(t, 0)
					});
					var o, i = [];
					for (e = 0; e < g.length; e++)o = new Date(1970, 0, 1, 0, b.minute - (n - e) * g.minuteStep), i.push({
						date: o,
						label: h(o, x),
						selected: f.$date && f.$isSelected(o, 1),
						disabled: f.$isDisabled(o, 1)
					});
					var r, s = [];
					for (e = 0; e < g.length; e++)r = new Date(1970, 0, 1, 0, 0, b.second - (n - e) * g.secondStep), s.push({
						date: r,
						label: h(r, T),
						selected: f.$date && f.$isSelected(r, 2),
						disabled: f.$isDisabled(r, 2)
					});
					var l = [];
					for (e = 0; e < g.length; e++)C ? l.push([a[e], i[e], s[e]]) : l.push([a[e], i[e]]);
					m.rows = l, m.showSeconds = C, m.showAM = E, m.isAM = (f.$date || a[n].date).getHours() < 12, m.timeSeparator = S, f.$isBuilt = !0
				}, f.$isSelected = function (e, t) {
					return f.$date ? 0 === t ? e.getHours() === f.$date.getHours() : 1 === t ? e.getMinutes() === f.$date.getMinutes() : 2 === t ? e.getSeconds() === f.$date.getSeconds() : void 0 : !1
				}, f.$isDisabled = function (e, t) {
					var n;
					return 0 === t ? n = e.getTime() + 6e4 * b.minute + 1e3 * b.second : 1 === t ? n = e.getTime() + 36e5 * b.hour + 1e3 * b.second : 2 === t && (n = e.getTime() + 36e5 * b.hour + 6e4 * b.minute), n < 1 * g.minTime || n > 1 * g.maxTime
				}, m.$arrowAction = function (e, t) {
					'picker' === g.arrowBehavior ? f.$setTimeByStep(e, t) : f.$moveIndex(e, t)
				}, f.$setTimeByStep = function (e, t) {
					var n = new Date(f.$date || y), a = n.getHours(), o = n.getMinutes(), i = n.getSeconds();
					0 === t ? n.setHours(a - parseInt(g.hourStep, 10) * e) : 1 === t ? n.setMinutes(o - parseInt(g.minuteStep, 10) * e) : 2 === t && n.setSeconds(i - parseInt(g.secondStep, 10) * e), f.select(n, t, !0)
				}, f.$moveIndex = function (e, t) {
					var n;
					0 === t ? (n = new Date(1970, 0, 1, b.hour + e * g.length, b.minute, b.second), angular.extend(b, {hour: n.getHours()})) : 1 === t ? (n = new Date(1970, 0, 1, b.hour, b.minute + e * g.length * g.minuteStep, b.second), angular.extend(b, {minute: n.getMinutes()})) : 2 === t && (n = new Date(1970, 0, 1, b.hour, b.minute, b.second + e * g.length * g.secondStep), angular.extend(b, {second: n.getSeconds()})), f.$build()
				}, f.$onMouseDown = function (e) {
					if ('input' !== e.target.nodeName.toLowerCase() && e.preventDefault(), e.stopPropagation(), c) {
						var t = angular.element(e.target);
						'button' !== t[0].nodeName.toLowerCase() && (t = t.parent()), t.triggerHandler('click')
					}
				}, f.$onKeyDown = function (e) {
					if (/(38|37|39|40|13)/.test(e.keyCode) && !e.shiftKey && !e.altKey) {
						if (e.preventDefault(), e.stopPropagation(), 13 === e.keyCode)return void f.hide(!0);
						var t = new Date(f.$date), n = t.getHours(), a = h(t, k).length, o = t.getMinutes(), i = h(t, x).length, r = t.getSeconds(), s = h(t, T).length, u = 1, c = /(37|39)/.test(e.keyCode), d = 2 + 1 * C + 1 * E;
						c && (37 === e.keyCode ? v = 1 > v ? d - 1 : v - 1 : 39 === e.keyCode && (v = d - 1 > v ? v + 1 : 0));
						var m = [0, a], $ = 0;
						38 === e.keyCode && ($ = -1), 40 === e.keyCode && ($ = 1);
						var w = 2 === v && C, y = 2 === v && !C || 3 === v && C;
						0 === v ? (t.setHours(n + $ * parseInt(g.hourStep, 10)), a = h(t, k).length, m = [0, a]) : 1 === v ? (t.setMinutes(o + $ * parseInt(g.minuteStep, 10)), i = h(t, x).length, m = [a + u, i]) : w ? (t.setSeconds(r + $ * parseInt(g.secondStep, 10)), s = h(t, T).length, m = [a + u + i + u, s]) : y && (c || f.switchMeridian(), m = [a + u + i + u + (s + u) * C, 2]), f.select(t, v, !0), l(m[0], m[1]), p.$digest()
					}
				};
				var M = f.init;
				f.init = function () {
					return u && g.useNative ? (t.prop('type', 'time'), void t.css('-webkit-appearance', 'textfield')) : (c && (t.prop('type', 'text'), t.attr('readonly', 'true'), t.on('click', d)), void M())
				};
				var A = f.destroy;
				f.destroy = function () {
					u && g.useNative && t.off('click', d), A()
				};
				var F = f.show;
				f.show = function () {
					!c && t.attr('readonly') || t.attr('disabled') || (F(), s(function () {
						f.$element && f.$element.on(c ? 'touchstart' : 'mousedown', f.$onMouseDown), g.keyboard && t && t.on('keydown', f.$onKeyDown)
					}, 0, !1))
				};
				var V = f.hide;
				return f.hide = function (e) {
					f.$isShown && (f.$element && f.$element.off(c ? 'touchstart' : 'mousedown', f.$onMouseDown), g.keyboard && t && t.off('keydown', f.$onKeyDown), V(e))
				}, f
			}

			var u = /(ip(a|o)d|iphone|android)/gi.test(t.navigator.userAgent), c = 'createTouch'in t.document && u;
			return e.lang || (e.lang = i.getDefaultLocale()), l.defaults = e, l
		}]
	}).directive('bsTimepicker', ['$window', '$parse', '$q', '$dateFormatter', '$dateParser', '$timepicker', function (e, t, a, o, i, r) {
		var s = r.defaults, l = /(ip(a|o)d|iphone|android)/gi.test(e.navigator.userAgent);
		return {
			restrict: 'EAC',
			require: 'ngModel',
			link: function (e, t, a, u) {
				function c(e) {
					if (angular.isDate(e)) {
						var t = isNaN(f.minTime) || new Date(e.getTime()).setFullYear(1970, 0, 1) >= f.minTime, n = isNaN(f.maxTime) || new Date(e.getTime()).setFullYear(1970, 0, 1) <= f.maxTime, a = t && n;
						u.$setValidity('date', a), u.$setValidity('min', t), u.$setValidity('max', n), a && (u.$dateValue = e)
					}
				}

				function d() {
					return !u.$dateValue || isNaN(u.$dateValue.getTime()) ? '' : $(u.$dateValue, f.timeFormat)
				}

				var f = {scope: e};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'placement', 'container', 'delay', 'trigger', 'keyboard', 'html', 'animation', 'autoclose', 'timeType', 'timeFormat', 'timezone', 'modelTimeFormat', 'useNative', 'hourStep', 'minuteStep', 'secondStep', 'length', 'arrowBehavior', 'iconUp', 'iconDown', 'roundDisplay', 'id', 'prefixClass', 'prefixEvent'], function (e) {
					angular.isDefined(a[e]) && (f[e] = a[e])
				});
				var p = /^(false|0|)$/i;
				angular.forEach(['html', 'container', 'autoclose', 'useNative', 'roundDisplay'], function (e) {
					angular.isDefined(a[e]) && p.test(a[e]) && (f[e] = !1)
				}), a.bsShow && e.$watch(a.bsShow, function (e, t) {
					g && angular.isDefined(e) && (angular.isString(e) && (e = !!e.match(/true|,?(timepicker),?/i)), e === !0 ? g.show() : g.hide())
				}), l && (f.useNative || s.useNative) && (f.timeFormat = 'HH:mm');
				var g = r(t, u, f);
				f = g.$options;
				var m = f.lang, $ = function (e, t, n) {
					return o.formatDate(e, t, m, n)
				}, h = i({format: f.timeFormat, lang: m});
				angular.forEach(['minTime', 'maxTime'], function (e) {
					angular.isDefined(a[e]) && a.$observe(e, function (t) {
						g.$options[e] = h.getTimeForAttribute(e, t), !isNaN(g.$options[e]) && g.$build(), c(u.$dateValue)
					})
				}), e.$watch(a.ngModel, function (e, t) {
					g.update(u.$dateValue)
				}, !0), u.$parsers.unshift(function (e) {
					var t;
					if (!e)return u.$setValidity('date', !0), null;
					var a = angular.isDate(e) ? e : h.parse(e, u.$dateValue);
					return !a || isNaN(a.getTime()) ? (u.$setValidity('date', !1), n) : (c(a), 'string' === f.timeType ? (t = h.timezoneOffsetAdjust(a, f.timezone, !0), $(t, f.modelTimeFormat || f.timeFormat)) : (t = h.timezoneOffsetAdjust(u.$dateValue, f.timezone, !0), 'number' === f.timeType ? t.getTime() : 'unix' === f.timeType ? t.getTime() / 1e3 : 'iso' === f.timeType ? t.toISOString() : new Date(t)))
				}), u.$formatters.push(function (e) {
					var t;
					return t = angular.isUndefined(e) || null === e ? NaN : angular.isDate(e) ? e : 'string' === f.timeType ? h.parse(e, null, f.modelTimeFormat) : 'unix' === f.timeType ? new Date(1e3 * e) : new Date(e), u.$dateValue = h.timezoneOffsetAdjust(t, f.timezone), d()
				}), u.$render = function () {
					t.val(d())
				}, e.$on('$destroy', function () {
					g && g.destroy(), f = null, g = null
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.tab', []).provider('$tab', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			template: 'tab/tab.tpl.html',
			navClass: 'nav-tabs',
			activeClass: 'active'
		}, t = this.controller = function (t, n, a) {
			var o = this;
			o.$options = angular.copy(e), angular.forEach(['animation', 'navClass', 'activeClass'], function (e) {
				angular.isDefined(a[e]) && (o.$options[e] = a[e])
			}), t.$navClass = o.$options.navClass, t.$activeClass = o.$options.activeClass, o.$panes = t.$panes = [], o.$activePaneChangeListeners = o.$viewChangeListeners = [], o.$push = function (e) {
				angular.isUndefined(o.$panes.$active) && t.$setActive(e.name || 0), o.$panes.push(e)
			}, o.$remove = function (e) {
				var t, n = o.$panes.indexOf(e), a = o.$panes.$active;
				t = angular.isString(a) ? o.$panes.map(function (e) {
					return e.name
				}).indexOf(a) : o.$panes.$active, o.$panes.splice(n, 1), t > n ? t-- : n === t && t === o.$panes.length && t--, t >= 0 && t < o.$panes.length ? o.$setActive(o.$panes[t].name || t) : o.$setActive()
			}, o.$setActive = t.$setActive = function (e) {
				o.$panes.$active = e, o.$activePaneChangeListeners.forEach(function (e) {
					e()
				})
			}, o.$isActive = t.$isActive = function (e, t) {
				return o.$panes.$active === e.name || o.$panes.$active === t
			}
		};
		this.$get = function () {
			var n = {};
			return n.defaults = e, n.controller = t, n
		}
	}).directive('bsTabs', ['$window', '$animate', '$tab', '$parse', function (e, t, n, a) {
		var o = n.defaults;
		return {
			require: ['?ngModel', 'bsTabs'],
			transclude: !0,
			scope: !0,
			controller: ['$scope', '$element', '$attrs', n.controller],
			templateUrl: function (e, t) {
				return t.template || o.template
			},
			link: function (e, t, n, o) {
				var i = o[0], r = o[1];
				if (i && (r.$activePaneChangeListeners.push(function () {
						i.$setViewValue(r.$panes.$active)
					}), i.$formatters.push(function (e) {
						return r.$setActive(e), e
					})), n.bsActivePane) {
					var s = a(n.bsActivePane);
					r.$activePaneChangeListeners.push(function () {
						s.assign(e, r.$panes.$active)
					}), e.$watch(n.bsActivePane, function (e, t) {
						r.$setActive(e)
					}, !0)
				}
			}
		}
	}]).directive('bsPane', ['$window', '$animate', '$sce', function (e, t, n) {
		return {
			require: ['^?ngModel', '^bsTabs'],
			scope: !0,
			link: function (e, a, o, i) {
				function r() {
					var n = s.$panes.indexOf(e);
					t[s.$isActive(e, n) ? 'addClass' : 'removeClass'](a, s.$options.activeClass)
				}

				var s = (i[0], i[1]);
				a.addClass('tab-pane'), o.$observe('title', function (t, a) {
					e.title = n.trustAsHtml(t)
				}), e.name = o.name, s.$options.animation && a.addClass(s.$options.animation), o.$observe('disabled', function (t, n) {
					e.disabled = e.$eval(t)
				}), s.$push(e), e.$on('$destroy', function () {
					s.$remove(e)
				}), s.$activePaneChangeListeners.push(function () {
					r()
				}), r()
			}
		}
	}]), angular.module('mgcrea.ngStrap.select', ['mgcrea.ngStrap.tooltip', 'mgcrea.ngStrap.helpers.parseOptions']).provider('$select', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			prefixClass: 'select',
			prefixEvent: '$select',
			placement: 'bottom-left',
			templateUrl: 'select/select.tpl.html',
			trigger: 'focus',
			container: !1,
			keyboard: !0,
			html: !1,
			delay: 0,
			multiple: !1,
			allNoneButtons: !1,
			sort: !0,
			caretHtml: '&nbsp;<span class="caret"></span>',
			placeholder: 'Choose among the following...',
			allText: 'All',
			noneText: 'None',
			maxLength: 3,
			maxLengthHtml: 'selected',
			iconCheckmark: 'glyphicon glyphicon-ok'
		};
		this.$get = ['$window', '$document', '$rootScope', '$tooltip', '$timeout', function (t, n, a, o, i) {
			function r(a, r, s) {
				var u = {}, c = angular.extend({}, e, s);
				u = o(a, c);
				var d = u.$scope;
				d.$matches = [], c.multiple ? d.$activeIndex = [] : d.$activeIndex = -1, d.$isMultiple = c.multiple, d.$showAllNoneButtons = c.allNoneButtons && c.multiple, d.$iconCheckmark = c.iconCheckmark, d.$allText = c.allText, d.$noneText = c.noneText, d.$activate = function (e) {
					d.$$postDigest(function () {
						u.activate(e)
					})
				}, d.$select = function (e, t) {
					d.$$postDigest(function () {
						u.select(e)
					})
				}, d.$isVisible = function () {
					return u.$isVisible()
				}, d.$isActive = function (e) {
					return u.$isActive(e)
				}, d.$selectAll = function () {
					for (var e = 0; e < d.$matches.length; e++)d.$isActive(e) || d.$select(e)
				}, d.$selectNone = function () {
					for (var e = 0; e < d.$matches.length; e++)d.$isActive(e) && d.$select(e)
				}, u.update = function (e) {
					d.$matches = e, u.$updateActiveIndex()
				}, u.activate = function (e) {
					return c.multiple ? (u.$isActive(e) ? d.$activeIndex.splice(d.$activeIndex.indexOf(e), 1) : d.$activeIndex.push(e), c.sort && d.$activeIndex.sort(function (e, t) {
						return e - t
					})) : d.$activeIndex = e, d.$activeIndex
				}, u.select = function (e) {
					var t = d.$matches[e].value;
					d.$apply(function () {
						u.activate(e), c.multiple ? r.$setViewValue(d.$activeIndex.map(function (e) {
							return angular.isUndefined(d.$matches[e]) ? null : d.$matches[e].value
						})) : (r.$setViewValue(t), u.hide())
					}), d.$emit(c.prefixEvent + '.select', t, e, u)
				}, u.$updateActiveIndex = function () {
					r.$modelValue && d.$matches.length ? c.multiple && angular.isArray(r.$modelValue) ? d.$activeIndex = r.$modelValue.map(function (e) {
						return u.$getIndex(e)
					}) : d.$activeIndex = u.$getIndex(r.$modelValue) : d.$activeIndex >= d.$matches.length ? d.$activeIndex = c.multiple ? [] : 0 : r.$modelValue || c.multiple || (d.$activeIndex = -1)
				}, u.$isVisible = function () {
					return c.minLength && r ? d.$matches.length && r.$viewValue.length >= c.minLength : d.$matches.length
				}, u.$isActive = function (e) {
					return c.multiple ? -1 !== d.$activeIndex.indexOf(e) : d.$activeIndex === e
				}, u.$getIndex = function (e) {
					var t = d.$matches.length, n = t;
					if (t) {
						for (n = t; n-- && d.$matches[n].value !== e;);
						if (!(0 > n))return n
					}
				}, u.$onMouseDown = function (e) {
					if (e.preventDefault(), e.stopPropagation(), l) {
						var t = angular.element(e.target);
						t.triggerHandler('click')
					}
				}, u.$onKeyDown = function (e) {
					return /(9|13|38|40)/.test(e.keyCode) ? (9 !== e.keyCode && (e.preventDefault(), e.stopPropagation()), c.multiple && 9 === e.keyCode ? u.hide() : c.multiple || 13 !== e.keyCode && 9 !== e.keyCode ? void(c.multiple || (38 === e.keyCode && d.$activeIndex > 0 ? d.$activeIndex-- : 38 === e.keyCode && d.$activeIndex < 0 ? d.$activeIndex = d.$matches.length - 1 : 40 === e.keyCode && d.$activeIndex < d.$matches.length - 1 ? d.$activeIndex++ : angular.isUndefined(d.$activeIndex) && (d.$activeIndex = 0), d.$digest())) : u.select(d.$activeIndex)) : void 0
				}, u.$isIE = function () {
					var e = t.navigator.userAgent;
					return e.indexOf('MSIE ') > 0 || e.indexOf('Trident/') > 0 || e.indexOf('Edge/') > 0
				}, u.$selectScrollFix = function (e) {
					'UL' === n[0].activeElement.tagName && (e.preventDefault(), e.stopImmediatePropagation(), e.target.focus())
				};
				var f = u.show;
				u.show = function () {
					f(), c.multiple && u.$element.addClass('select-multiple'), i(function () {
						u.$element.on(l ? 'touchstart' : 'mousedown', u.$onMouseDown), c.keyboard && a.on('keydown', u.$onKeyDown)
					}, 0, !1)
				};
				var p = u.hide;
				return u.hide = function () {
					c.multiple || r.$modelValue || (d.$activeIndex = -1), u.$element.off(l ? 'touchstart' : 'mousedown', u.$onMouseDown), c.keyboard && a.off('keydown', u.$onKeyDown), p(!0)
				}, u
			}

			var s = (angular.element(t.document.body), /(ip(a|o)d|iphone|android)/gi.test(t.navigator.userAgent)), l = 'createTouch'in t.document && s;
			return r.defaults = e, r
		}]
	}).directive('bsSelect', ['$window', '$parse', '$q', '$select', '$parseOptions', function (e, t, n, a, o) {
		var i = a.defaults;
		return {
			restrict: 'EAC', require: 'ngModel', link: function (e, t, n, r) {
				var s = {scope: e, placeholder: i.placeholder};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'placement', 'container', 'delay', 'trigger', 'keyboard', 'html', 'animation', 'placeholder', 'allNoneButtons', 'maxLength', 'maxLengthHtml', 'allText', 'noneText', 'iconCheckmark', 'autoClose', 'id', 'sort', 'caretHtml', 'prefixClass', 'prefixEvent'], function (e) {
					angular.isDefined(n[e]) && (s[e] = n[e])
				});
				var l = /^(false|0|)$/i;
				angular.forEach(['html', 'container', 'allNoneButtons', 'sort'], function (e) {
					angular.isDefined(n[e]) && l.test(n[e]) && (s[e] = !1)
				});
				var u = t.attr('data-multiple');
				if (angular.isDefined(u) && (l.test(u) ? s.multiple = !1 : s.multiple = u), 'select' === t[0].nodeName.toLowerCase()) {
					var c = t;
					c.css('display', 'none'), t = angular.element('<button type="button" class="btn btn-default"></button>'), c.after(t)
				}
				var d = o(n.bsOptions), f = a(t, r, s);
				f.$isIE() && t[0].addEventListener('blur', f.$selectScrollFix);
				var p = d.$match[7].replace(/\|.+/, '').trim();
				e.$watchCollection(p, function (t, n) {
					d.valuesFn(e, r).then(function (e) {
						f.update(e), r.$render()
					})
				}), e.$watch(n.ngModel, function (e, t) {
					f.$updateActiveIndex(), r.$render()
				}, !0), r.$render = function () {
					var e, n;
					s.multiple && angular.isArray(r.$modelValue) ? (e = r.$modelValue.map(function (e) {
						return n = f.$getIndex(e), angular.isDefined(n) ? f.$scope.$matches[n].label : !1
					}).filter(angular.isDefined), e = e.length > (s.maxLength || i.maxLength) ? e.length + ' ' + (s.maxLengthHtml || i.maxLengthHtml) : e.join(', ')) : (n = f.$getIndex(r.$modelValue), e = angular.isDefined(n) ? f.$scope.$matches[n].label : !1), t.html((e ? e : s.placeholder) + (s.caretHtml ? s.caretHtml : i.caretHtml))
				}, s.multiple && (r.$isEmpty = function (e) {
					return !e || 0 === e.length
				}), e.$on('$destroy', function () {
					f && f.destroy(), s = null, f = null
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.scrollspy', ['mgcrea.ngStrap.helpers.debounce', 'mgcrea.ngStrap.helpers.dimensions']).provider('$scrollspy', function () {
		var e = this.$$spies = {}, n = this.defaults = {
			debounce: 150,
			throttle: 100,
			offset: 100
		};
		this.$get = ['$window', '$document', '$rootScope', 'dimensions', 'debounce', 'throttle', function (a, o, i, r, s, l) {
			function u(e, t) {
				return e[0].nodeName && e[0].nodeName.toLowerCase() === t.toLowerCase()
			}

			function c(o) {
				var c = angular.extend({}, n, o);
				c.element || (c.element = p);
				var g = u(c.element, 'body'), m = g ? d : c.element, $ = g ? 'window' : c.id;
				if (e[$])return e[$].$$count++, e[$];
				var h, v, w, y, b, D, k, S, x = {}, T = x.$trackedElements = [], C = [];
				return x.init = function () {
					this.$$count = 1, y = s(this.checkPosition, c.debounce), b = l(this.checkPosition, c.throttle), m.on('click', this.checkPositionWithEventLoop), d.on('resize', y), m.on('scroll', b), D = s(this.checkOffsets, c.debounce), h = i.$on('$viewContentLoaded', D), v = i.$on('$includeContentLoaded', D), D(), $ && (e[$] = x)
				}, x.destroy = function () {
					this.$$count--, this.$$count > 0 || (m.off('click', this.checkPositionWithEventLoop), d.off('resize', y), m.off('scroll', b), h(), v(), $ && delete e[$])
				}, x.checkPosition = function () {
					if (C.length) {
						if (S = (g ? a.pageYOffset : m.prop('scrollTop')) || 0, k = Math.max(a.innerHeight, f.prop('clientHeight')), S < C[0].offsetTop && w !== C[0].target)return x.$activateElement(C[0]);
						for (var e = C.length; e--;)if (!angular.isUndefined(C[e].offsetTop) && null !== C[e].offsetTop && w !== C[e].target && !(S < C[e].offsetTop || C[e + 1] && S > C[e + 1].offsetTop))return x.$activateElement(C[e])
					}
				}, x.checkPositionWithEventLoop = function () {
					setTimeout(x.checkPosition, 1)
				}, x.$activateElement = function (e) {
					if (w) {
						var t = x.$getTrackedElement(w);
						t && (t.source.removeClass('active'), u(t.source, 'li') && u(t.source.parent().parent(), 'li') && t.source.parent().parent().removeClass('active'))
					}
					w = e.target, e.source.addClass('active'), u(e.source, 'li') && u(e.source.parent().parent(), 'li') && e.source.parent().parent().addClass('active')
				}, x.$getTrackedElement = function (e) {
					return T.filter(function (t) {
						return t.target === e
					})[0]
				}, x.checkOffsets = function () {
					angular.forEach(T, function (e) {
						var n = t.querySelector(e.target);
						e.offsetTop = n ? r.offset(n).top : null, c.offset && null !== e.offsetTop && (e.offsetTop -= 1 * c.offset)
					}), C = T.filter(function (e) {
						return null !== e.offsetTop
					}).sort(function (e, t) {
						return e.offsetTop - t.offsetTop
					}), y()
				}, x.trackElement = function (e, t) {
					T.push({target: e, source: t})
				}, x.untrackElement = function (e, t) {
					for (var n, a = T.length; a--;)if (T[a].target === e && T[a].source === t) {
						n = a;
						break
					}
					T = T.splice(n, 1)
				}, x.activate = function (e) {
					T[e].addClass('active')
				}, x.init(), x
			}

			var d = angular.element(a), f = angular.element(o.prop('documentElement')), p = angular.element(a.document.body);
			return c
		}]
	}).directive('bsScrollspy', ['$rootScope', 'debounce', 'dimensions', '$scrollspy', function (e, t, n, a) {
		return {
			restrict: 'EAC', link: function (e, t, n) {
				var o = {scope: e};
				angular.forEach(['offset', 'target'], function (e) {
					angular.isDefined(n[e]) && (o[e] = n[e])
				});
				var i = a(o);
				i.trackElement(o.target, t), e.$on('$destroy', function () {
					i && (i.untrackElement(o.target, t), i.destroy()), o = null, i = null
				})
			}
		}
	}]).directive('bsScrollspyList', ['$rootScope', 'debounce', 'dimensions', '$scrollspy', function (e, t, n, a) {
		return {
			restrict: 'A', compile: function (e, t) {
				var n = e[0].querySelectorAll('li > a[href]');
				angular.forEach(n, function (e) {
					var t = angular.element(e);
					t.parent().attr('bs-scrollspy', '').attr('data-target', t.attr('href'))
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.popover', ['mgcrea.ngStrap.tooltip']).provider('$popover', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			customClass: '',
			container: !1,
			target: !1,
			placement: 'right',
			templateUrl: 'popover/popover.tpl.html',
			contentTemplate: !1,
			trigger: 'click',
			keyboard: !0,
			html: !1,
			title: '',
			content: '',
			delay: 0,
			autoClose: !1
		};
		this.$get = ['$tooltip', function (t) {
			function n(n, a) {
				var o = angular.extend({}, e, a), i = t(n, o);
				return o.content && (i.$scope.content = o.content), i
			}

			return n
		}]
	}).directive('bsPopover', ['$window', '$sce', '$popover', function (e, t, n) {
		var a = e.requestAnimationFrame || e.setTimeout;
		return {
			restrict: 'EAC', scope: !0, link: function (e, o, i) {
				var r = {scope: e};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'contentTemplate', 'placement', 'container', 'delay', 'trigger', 'html', 'animation', 'customClass', 'autoClose', 'id', 'prefixClass', 'prefixEvent'], function (e) {
					angular.isDefined(i[e]) && (r[e] = i[e])
				});
				var s = /^(false|0|)$/i;
				angular.forEach(['html', 'container', 'autoClose'], function (e) {
					angular.isDefined(i[e]) && s.test(i[e]) && (r[e] = !1)
				});
				var l = o.attr('data-target');
				angular.isDefined(l) && (s.test(l) ? r.target = !1 : r.target = l), angular.forEach(['title', 'content'], function (n) {
					i[n] && i.$observe(n, function (o, i) {
						e[n] = t.trustAsHtml(o), angular.isDefined(i) && a(function () {
							u && u.$applyPlacement()
						})
					})
				}), i.bsPopover && e.$watch(i.bsPopover, function (t, n) {
					angular.isObject(t) ? angular.extend(e, t) : e.content = t, angular.isDefined(n) && a(function () {
						u && u.$applyPlacement()
					})
				}, !0), i.bsShow && e.$watch(i.bsShow, function (e, t) {
					u && angular.isDefined(e) && (angular.isString(e) && (e = !!e.match(/true|,?(popover),?/i)), e === !0 ? u.show() : u.hide())
				}), i.viewport && e.$watch(i.viewport, function (e) {
					u && angular.isDefined(e) && u.setViewport(e)
				});
				var u = n(o, r);
				e.$on('$destroy', function () {
					u && u.destroy(), r = null, u = null
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.navbar', []).provider('$navbar', function () {
		var e = this.defaults = {
			activeClass: 'active',
			routeAttr: 'data-match-route',
			strict: !1
		};
		this.$get = function () {
			return {defaults: e}
		}
	}).directive('bsNavbar', ['$window', '$location', '$navbar', function (e, t, n) {
		var a = n.defaults;
		return {
			restrict: 'A', link: function (e, n, o, i) {
				var r = angular.copy(a);
				angular.forEach(Object.keys(a), function (e) {
					angular.isDefined(o[e]) && (r[e] = o[e])
				}), e.$watch(function () {
					return t.path()
				}, function (e, t) {
					var a = n[0].querySelectorAll('li[' + r.routeAttr + ']');
					angular.forEach(a, function (t) {
						var n = angular.element(t), a = n.attr(r.routeAttr).replace('/', '\\/');
						r.strict && (a = '^' + a + '$');
						var o = new RegExp(a, 'i');
						o.test(e) ? n.addClass(r.activeClass) : n.removeClass(r.activeClass)
					})
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.modal', ['mgcrea.ngStrap.core', 'mgcrea.ngStrap.helpers.dimensions']).provider('$modal', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			backdropAnimation: 'am-fade',
			prefixClass: 'modal',
			prefixEvent: 'modal',
			placement: 'top',
			templateUrl: 'modal/modal.tpl.html',
			template: '',
			contentTemplate: !1,
			container: !1,
			element: null,
			backdrop: !0,
			keyboard: !0,
			html: !1,
			show: !0
		};
		this.$get = ['$window', '$rootScope', '$bsCompiler', '$animate', '$timeout', '$sce', 'dimensions', function (n, a, o, i, r, s, l) {
			function u(t) {
				function n() {
					k.$emit(b.prefixEvent + '.show', y)
				}

				function r() {
					k.$emit(b.prefixEvent + '.hide', y), g.removeClass(b.prefixClass + '-open'), b.animation && g.removeClass(b.prefixClass + '-with-' + b.animation)
				}

				function l() {
					b.backdrop && (x.on('click', h), C.on('click', h), C.on('wheel', v))
				}

				function u() {
					b.backdrop && (x.off('click', h), C.off('click', h), C.off('wheel', v))
				}

				function m() {
					b.keyboard && x.on('keyup', y.$onKeyUp)
				}

				function $() {
					b.keyboard && x.off('keyup', y.$onKeyUp)
				}

				function h(e) {
					e.target === e.currentTarget && ('static' === b.backdrop ? y.focus() : y.hide())
				}

				function v(e) {
					e.preventDefault()
				}

				function w() {
					y.$isShown && null !== x && (u(), $()), T && (T.$destroy(), T = null), x && (x.remove(), x = y.$element = null)
				}

				var y = {}, b = y.$options = angular.extend({}, e, t), D = y.$promise = o.compile(b), k = y.$scope = b.scope && b.scope.$new() || a.$new();
				b.element || b.container || (b.container = 'body'), y.$id = b.id || b.element && b.element.attr('id') || '', f(['title', 'content'], function (e) {
					b[e] && (k[e] = s.trustAsHtml(b[e]))
				}), k.$hide = function () {
					k.$$postDigest(function () {
						y.hide()
					})
				}, k.$show = function () {
					k.$$postDigest(function () {
						y.show()
					})
				}, k.$toggle = function () {
					k.$$postDigest(function () {
						y.toggle()
					})
				}, y.$isShown = k.$isShown = !1;
				var S, x, T, C = angular.element('<div class="' + b.prefixClass + '-backdrop"/>');
				return C.css({
					position: 'fixed',
					top: '0px',
					left: '0px',
					bottom: '0px',
					right: '0px',
					'z-index': 1038
				}), D.then(function (e) {
					S = e, y.init()
				}), y.init = function () {
					b.show && k.$$postDigest(function () {
						y.show()
					})
				}, y.destroy = function () {
					w(), C && (C.remove(), C = null), k.$destroy()
				}, y.show = function () {
					if (!y.$isShown) {
						var e, t;
						if (angular.isElement(b.container) ? (e = b.container, t = b.container[0].lastChild ? angular.element(b.container[0].lastChild) : null) : b.container ? (e = d(b.container), t = e[0] && e[0].lastChild ? angular.element(e[0].lastChild) : null) : (e = null, t = b.element), x && w(), T = y.$scope.$new(), x = y.$element = S.link(T, function (e, t) {
							}), !k.$emit(b.prefixEvent + '.show.before', y).defaultPrevented) {
							x.css({display: 'block'}).addClass(b.placement), b.animation && (b.backdrop && C.addClass(b.backdropAnimation), x.addClass(b.animation)), b.backdrop && i.enter(C, g, null), angular.version.minor <= 2 ? i.enter(x, e, t, n) : i.enter(x, e, t).then(n), y.$isShown = k.$isShown = !0, c(k);
							var a = x[0];
							p(function () {
								a.focus()
							}), g.addClass(b.prefixClass + '-open'), b.animation && g.addClass(b.prefixClass + '-with-' + b.animation), l(), m()
						}
					}
				}, y.hide = function () {
					y.$isShown && (k.$emit(b.prefixEvent + '.hide.before', y).defaultPrevented || (angular.version.minor <= 2 ? i.leave(x, r) : i.leave(x).then(r), b.backdrop && i.leave(C), y.$isShown = k.$isShown = !1, c(k), u(), $()))
				}, y.toggle = function () {
					y.$isShown ? y.hide() : y.show()
				}, y.focus = function () {
					x[0].focus()
				}, y.$onKeyUp = function (e) {
					27 === e.which && y.$isShown && (y.hide(), e.stopPropagation())
				}, y
			}

			function c(e) {
				e.$$phase || e.$root && e.$root.$$phase || e.$digest()
			}

			function d(e, n) {
				return angular.element((n || t).querySelectorAll(e))
			}

			var f = angular.forEach, p = (String.prototype.trim, n.requestAnimationFrame || n.setTimeout), g = angular.element(n.document.body);
			return u
		}]
	}).directive('bsModal', ['$window', '$sce', '$modal', function (e, t, n) {
		return {
			restrict: 'EAC', scope: !0, link: function (e, a, o, i) {
				var r = {scope: e, element: a, show: !1};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'contentTemplate', 'placement', 'backdrop', 'keyboard', 'html', 'container', 'animation', 'backdropAnimation', 'id', 'prefixEvent', 'prefixClass'], function (e) {
					angular.isDefined(o[e]) && (r[e] = o[e])
				});
				var s = /^(false|0|)$/i;
				angular.forEach(['backdrop', 'keyboard', 'html', 'container'], function (e) {
					angular.isDefined(o[e]) && s.test(o[e]) && (r[e] = !1)
				}), angular.forEach(['title', 'content'], function (n) {
					o[n] && o.$observe(n, function (a, o) {
						e[n] = t.trustAsHtml(a)
					})
				}), o.bsModal && e.$watch(o.bsModal, function (t, n) {
					angular.isObject(t) ? angular.extend(e, t) : e.content = t
				}, !0);
				var l = n(r);
				a.on(o.trigger || 'click', l.toggle), e.$on('$destroy', function () {
					l && l.destroy(), r = null, l = null
				})
			}
		}
	}]), angular.version.minor < 3 && angular.version.dot < 14 && angular.module('ng').factory('$$rAF', ['$window', '$timeout', function (e, t) {
		var n = e.requestAnimationFrame || e.webkitRequestAnimationFrame || e.mozRequestAnimationFrame, a = e.cancelAnimationFrame || e.webkitCancelAnimationFrame || e.mozCancelAnimationFrame || e.webkitCancelRequestAnimationFrame, o = !!n, i = o ? function (e) {
			var t = n(e);
			return function () {
				a(t)
			}
		} : function (e) {
			var n = t(e, 16.66, !1);
			return function () {
				t.cancel(n)
			}
		};
		return i.supported = o, i
	}]), angular.module('mgcrea.ngStrap.helpers.parseOptions', []).provider('$parseOptions', function () {
		var e = this.defaults = {regexp: /^\s*(.*?)(?:\s+as\s+(.*?))?(?:\s+group\s+by\s+(.*))?\s+for\s+(?:([\$\w][\$\w]*)|(?:\(\s*([\$\w][\$\w]*)\s*,\s*([\$\w][\$\w]*)\s*\)))\s+in\s+(.*?)(?:\s+track\s+by\s+(.*?))?$/};
		this.$get = ['$parse', '$q', function (t, n) {
			function a(a, o) {
				function i(e, t) {
					return e.map(function (e, n) {
						var a, o, i = {};
						return i[c] = e, a = u(t, i), o = p(t, i), {
							label: a,
							value: o,
							index: n
						}
					})
				}

				var r = {}, s = angular.extend({}, e, o);
				r.$values = [];
				var l, u, c, d, f, p, g;
				return r.init = function () {
					r.$match = l = a.match(s.regexp), u = t(l[2] || l[1]), c = l[4] || l[6], d = l[5], f = t(l[3] || ''), p = t(l[2] ? l[1] : c), g = t(l[7])
				}, r.valuesFn = function (e, t) {
					return n.when(g(e, t)).then(function (t) {
						return angular.isArray(t) || (t = []), r.$values = t.length ? i(t, e) : [], r.$values
					})
				}, r.displayValue = function (e) {
					var t = {};
					return t[c] = e, u(t)
				}, r.init(), r
			}

			return a
		}]
	}), angular.module('mgcrea.ngStrap.helpers.dimensions', []).factory('dimensions', ['$document', '$window', function (t, n) {
		var a = (angular.element, {}), o = a.nodeName = function (e, t) {
			return e.nodeName && e.nodeName.toLowerCase() === t.toLowerCase()
		};
		a.css = function (t, n, a) {
			var o;
			return o = t.currentStyle ? t.currentStyle[n] : e.getComputedStyle ? e.getComputedStyle(t)[n] : t.style[n], a === !0 ? parseFloat(o) || 0 : o
		}, a.offset = function (t) {
			var n = t.getBoundingClientRect(), a = t.ownerDocument;
			return {
				width: n.width || t.offsetWidth,
				height: n.height || t.offsetHeight,
				top: n.top + (e.pageYOffset || a.documentElement.scrollTop) - (a.documentElement.clientTop || 0),
				left: n.left + (e.pageXOffset || a.documentElement.scrollLeft) - (a.documentElement.clientLeft || 0)
			}
		}, a.setOffset = function (e, t, n) {
			var o, i, r, s, l, u, c, d = a.css(e, 'position'), f = angular.element(e), p = {};
			'static' === d && (e.style.position = 'relative'), l = a.offset(e), r = a.css(e, 'top'), u = a.css(e, 'left'), c = ('absolute' === d || 'fixed' === d) && (r + u).indexOf('auto') > -1, c ? (o = a.position(e), s = o.top, i = o.left) : (s = parseFloat(r) || 0, i = parseFloat(u) || 0), angular.isFunction(t) && (t = t.call(e, n, l)), null !== t.top && (p.top = t.top - l.top + s), null !== t.left && (p.left = t.left - l.left + i), 'using'in t ? t.using.call(f, p) : f.css({
				top: p.top + 'px',
				left: p.left + 'px'
			})
		}, a.position = function (e) {
			var t, n, r = {top: 0, left: 0};
			return 'fixed' === a.css(e, 'position') ? n = e.getBoundingClientRect() : (t = i(e), n = a.offset(e), o(t, 'html') || (r = a.offset(t)), r.top += a.css(t, 'borderTopWidth', !0), r.left += a.css(t, 'borderLeftWidth', !0)), {
				width: e.offsetWidth,
				height: e.offsetHeight,
				top: n.top - r.top - a.css(e, 'marginTop', !0),
				left: n.left - r.left - a.css(e, 'marginLeft', !0)
			}
		};
		var i = function (e) {
			var t = e.ownerDocument, n = e.offsetParent || t;
			if (o(n, '#document'))return t.documentElement;
			for (; n && !o(n, 'html') && 'static' === a.css(n, 'position');)n = n.offsetParent;
			return n || t.documentElement
		};
		return a.height = function (e, t) {
			var n = e.offsetHeight;
			return t ? n += a.css(e, 'marginTop', !0) + a.css(e, 'marginBottom', !0) : n -= a.css(e, 'paddingTop', !0) + a.css(e, 'paddingBottom', !0) + a.css(e, 'borderTopWidth', !0) + a.css(e, 'borderBottomWidth', !0), n
		}, a.width = function (e, t) {
			var n = e.offsetWidth;
			return t ? n += a.css(e, 'marginLeft', !0) + a.css(e, 'marginRight', !0) : n -= a.css(e, 'paddingLeft', !0) + a.css(e, 'paddingRight', !0) + a.css(e, 'borderLeftWidth', !0) + a.css(e, 'borderRightWidth', !0), n
		}, a
	}]), angular.module('mgcrea.ngStrap.helpers.debounce', []).factory('debounce', ['$timeout', function (e) {
		return function (t, n, a) {
			var o = null;
			return function () {
				var i = this, r = arguments, s = a && !o;
				return o && e.cancel(o), o = e(function () {
					o = null, a || t.apply(i, r)
				}, n, !1), s && t.apply(i, r), o
			}
		}
	}]).factory('throttle', ['$timeout', function (e) {
		return function (t, n, a) {
			var o = null;
			return a || (a = {}), function () {
				var i = this, r = arguments;
				o || (a.leading !== !1 && t.apply(i, r), o = e(function () {
					o = null, a.trailing !== !1 && t.apply(i, r)
				}, n, !1))
			}
		}
	}]), angular.module('mgcrea.ngStrap.helpers.dateParser', []).provider('$dateParser', ['$localeProvider', function (e) {
		function t() {
			this.year = 1970, this.month = 0, this.day = 1, this.hours = 0, this.minutes = 0, this.seconds = 0, this.milliseconds = 0
		}

		function n() {
		}

		function a(e) {
			return !isNaN(parseFloat(e)) && isFinite(e)
		}

		function o(e, t) {
			for (var n = e.length, a = t.toString().toLowerCase(), o = 0; n > o; o++)if (e[o].toLowerCase() === a)return o;
			return -1
		}

		t.prototype.setMilliseconds = function (e) {
			this.milliseconds = e
		}, t.prototype.setSeconds = function (e) {
			this.seconds = e
		}, t.prototype.setMinutes = function (e) {
			this.minutes = e
		}, t.prototype.setHours = function (e) {
			this.hours = e
		}, t.prototype.getHours = function () {
			return this.hours
		}, t.prototype.setDate = function (e) {
			this.day = e
		}, t.prototype.setMonth = function (e) {
			this.month = e
		}, t.prototype.setFullYear = function (e) {
			this.year = e
		}, t.prototype.fromDate = function (e) {
			return this.year = e.getFullYear(), this.month = e.getMonth(), this.day = e.getDate(), this.hours = e.getHours(), this.minutes = e.getMinutes(), this.seconds = e.getSeconds(), this.milliseconds = e.getMilliseconds(), this
		}, t.prototype.toDate = function () {
			return new Date(this.year, this.month, this.day, this.hours, this.minutes, this.seconds, this.milliseconds)
		};
		var i = t.prototype, r = this.defaults = {
			format: 'shortDate',
			strict: !1
		};
		this.$get = ['$locale', 'dateFilter', function (e, s) {
			var l = function (l) {
				function u(e) {
					var t, n = Object.keys(h), a = [], o = [], i = e;
					for (t = 0; t < n.length; t++)if (e.split(n[t]).length > 1) {
						var r = i.search(n[t]);
						e = e.split(n[t]).join(''), h[n[t]] && (a[r] = h[n[t]])
					}
					return angular.forEach(a, function (e) {
						e && o.push(e)
					}), o
				}

				function c(e) {
					return e.replace(/\//g, '[\\/]').replace('/-/g', '[-]').replace(/\./g, '[.]').replace(/\\s/g, '[\\s]')
				}

				function d(e) {
					var t, n = Object.keys($), a = e;
					for (t = 0; t < n.length; t++)a = a.split(n[t]).join('${' + t + '}');
					for (t = 0; t < n.length; t++)a = a.split('${' + t + '}').join('(' + $[n[t]] + ')');
					return e = c(e), new RegExp('^' + a + '$', ['i'])
				}

				var f, p, g = angular.extend({}, r, l), m = {}, $ = {
					sss: '[0-9]{3}',
					ss: '[0-5][0-9]',
					s: g.strict ? '[1-5]?[0-9]' : '[0-9]|[0-5][0-9]',
					mm: '[0-5][0-9]',
					m: g.strict ? '[1-5]?[0-9]' : '[0-9]|[0-5][0-9]',
					HH: '[01][0-9]|2[0-3]',
					H: g.strict ? '1?[0-9]|2[0-3]' : '[01]?[0-9]|2[0-3]',
					hh: '[0][1-9]|[1][012]',
					h: g.strict ? '[1-9]|1[012]' : '0?[1-9]|1[012]',
					a: 'AM|PM',
					EEEE: e.DATETIME_FORMATS.DAY.join('|'),
					EEE: e.DATETIME_FORMATS.SHORTDAY.join('|'),
					dd: '0[1-9]|[12][0-9]|3[01]',
					d: g.strict ? '[1-9]|[1-2][0-9]|3[01]' : '0?[1-9]|[1-2][0-9]|3[01]',
					MMMM: e.DATETIME_FORMATS.MONTH.join('|'),
					MMM: e.DATETIME_FORMATS.SHORTMONTH.join('|'),
					MM: '0[1-9]|1[012]',
					M: g.strict ? '[1-9]|1[012]' : '0?[1-9]|1[012]',
					yyyy: '[1]{1}[0-9]{3}|[2]{1}[0-9]{3}',
					yy: '[0-9]{2}',
					y: g.strict ? '-?(0|[1-9][0-9]{0,3})' : '-?0*[0-9]{1,4}'
				}, h = {
					sss: i.setMilliseconds,
					ss: i.setSeconds,
					s: i.setSeconds,
					mm: i.setMinutes,
					m: i.setMinutes,
					HH: i.setHours,
					H: i.setHours,
					hh: i.setHours,
					h: i.setHours,
					EEEE: n,
					EEE: n,
					dd: i.setDate,
					d: i.setDate,
					a: function (e) {
						var t = this.getHours() % 12;
						return this.setHours(e.match(/pm/i) ? t + 12 : t)
					},
					MMMM: function (t) {
						return this.setMonth(o(e.DATETIME_FORMATS.MONTH, t))
					},
					MMM: function (t) {
						return this.setMonth(o(e.DATETIME_FORMATS.SHORTMONTH, t))
					},
					MM: function (e) {
						return this.setMonth(1 * e - 1)
					},
					M: function (e) {
						return this.setMonth(1 * e - 1)
					},
					yyyy: i.setFullYear,
					yy: function (e) {
						return this.setFullYear(2e3 + 1 * e)
					},
					y: function (e) {
						return 50 >= 1 * e && 2 === e.length ? this.setFullYear(2e3 + 1 * e) : this.setFullYear(1 * e)
					}
				};
				return m.init = function () {
					m.$format = e.DATETIME_FORMATS[g.format] || g.format, f = d(m.$format), p = u(m.$format)
				}, m.isValid = function (e) {
					return angular.isDate(e) ? !isNaN(e.getTime()) : f.test(e)
				}, m.parse = function (n, a, o, i) {
					o && (o = e.DATETIME_FORMATS[o] || o), angular.isDate(n) && (n = s(n, o || m.$format, i));
					var r = o ? d(o) : f, l = o ? u(o) : p, c = r.exec(n);
					if (!c)return !1;
					for (var g = a && !isNaN(a.getTime()) ? (new t).fromDate(a) : (new t).fromDate(new Date(1970, 0, 1, 0)), $ = 0; $ < c.length - 1; $++)l[$] && l[$].call(g, c[$ + 1]);
					var h = g.toDate();
					return parseInt(g.day, 10) !== h.getDate() ? !1 : h
				}, m.getDateForAttribute = function (e, t) {
					var n;
					if ('today' === t) {
						var o = new Date;
						n = new Date(o.getFullYear(), o.getMonth(), o.getDate() + ('maxDate' === e ? 1 : 0), 0, 0, 0, 'minDate' === e ? 0 : -1)
					} else n = angular.isString(t) && t.match(/^".+"$/) ? new Date(t.substr(1, t.length - 2)) : a(t) ? new Date(parseInt(t, 10)) : angular.isString(t) && 0 === t.length ? 'minDate' === e ? -(1 / 0) : +(1 / 0) : new Date(t);
					return n
				}, m.getTimeForAttribute = function (e, t) {
					var n;
					return n = 'now' === t ? (new Date).setFullYear(1970, 0, 1) : angular.isString(t) && t.match(/^".+"$/) ? new Date(t.substr(1, t.length - 2)).setFullYear(1970, 0, 1) : a(t) ? new Date(parseInt(t, 10)).setFullYear(1970, 0, 1) : angular.isString(t) && 0 === t.length ? 'minTime' === e ? -(1 / 0) : +(1 / 0) : m.parse(t, new Date(1970, 0, 1, 0))
				}, m.daylightSavingAdjust = function (e) {
					return e ? (e.setHours(e.getHours() > 12 ? e.getHours() + 2 : 0), e) : null
				}, m.timezoneOffsetAdjust = function (e, t, n) {
					return e ? (t && 'UTC' === t && (e = new Date(e.getTime()), e.setMinutes(e.getMinutes() + (n ? -1 : 1) * e.getTimezoneOffset())), e) : null
				}, m.init(), m
			};
			return l
		}]
	}]), angular.module('mgcrea.ngStrap.helpers.dateFormatter', []).service('$dateFormatter', ['$locale', 'dateFilter', function (e, t) {
		function n(e) {
			return /(h+)([:\.])?(m+)([:\.])?(s*)[ ]?(a?)/i.exec(e).slice(1)
		}

		this.getDefaultLocale = function () {
			return e.id
		}, this.getDatetimeFormat = function (t, n) {
			return e.DATETIME_FORMATS[t] || t
		}, this.weekdaysShort = function (t) {
			return e.DATETIME_FORMATS.SHORTDAY
		}, this.hoursFormat = function (e) {
			return n(e)[0]
		}, this.minutesFormat = function (e) {
			return n(e)[2]
		}, this.secondsFormat = function (e) {
			return n(e)[4]
		}, this.timeSeparator = function (e) {
			return n(e)[1]
		}, this.showSeconds = function (e) {
			return !!n(e)[4]
		}, this.showAM = function (e) {
			return !!n(e)[5]
		}, this.formatDate = function (e, n, a, o) {
			return t(e, n, o)
		}
	}]), angular.module('mgcrea.ngStrap.core', []).service('$bsCompiler', a), a.$inject = ['$q', '$http', '$injector', '$compile', '$controller', '$templateCache'], angular.module('mgcrea.ngStrap.dropdown', ['mgcrea.ngStrap.tooltip']).provider('$dropdown', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			prefixClass: 'dropdown',
			prefixEvent: 'dropdown',
			placement: 'bottom-left',
			templateUrl: 'dropdown/dropdown.tpl.html',
			trigger: 'click',
			container: !1,
			keyboard: !0,
			html: !1,
			delay: 0
		};
		this.$get = ['$window', '$rootScope', '$tooltip', '$timeout', function (t, n, a, o) {
			function i(t, i) {
				function l(e) {
					return e.target !== t[0] ? e.target !== t[0] && u.hide() : void 0
				}

				var u = {}, c = angular.extend({}, e, i);
				u.$scope = c.scope && c.scope.$new() || n.$new();
				u = a(t, c);
				var d = t.parent();
				u.$onKeyDown = function (e) {
					if (/(38|40)/.test(e.keyCode)) {
						e.preventDefault(), e.stopPropagation();
						var t = angular.element(u.$element[0].querySelectorAll('li:not(.divider) a'));
						if (t.length) {
							var n;
							angular.forEach(t, function (e, t) {
								s && s.call(e, ':focus') && (n = t)
							}), 38 === e.keyCode && n > 0 ? n-- : 40 === e.keyCode && n < t.length - 1 ? n++ : angular.isUndefined(n) && (n = 0), t.eq(n)[0].focus()
						}
					}
				};
				var f = u.show;
				u.show = function () {
					f(), o(function () {
						c.keyboard && u.$element && u.$element.on('keydown', u.$onKeyDown), r.on('click', l)
					}, 0, !1), d.hasClass('dropdown') && d.addClass('open')
				};
				var p = u.hide;
				u.hide = function () {
					u.$isShown && (c.keyboard && u.$element && u.$element.off('keydown', u.$onKeyDown), r.off('click', l), d.hasClass('dropdown') && d.removeClass('open'), p())
				};
				var g = u.destroy;
				return u.destroy = function () {
					r.off('click', l), g()
				}, u
			}

			var r = angular.element(t.document.body), s = Element.prototype.matchesSelector || Element.prototype.webkitMatchesSelector || Element.prototype.mozMatchesSelector || Element.prototype.msMatchesSelector || Element.prototype.oMatchesSelector;
			return i
		}]
	}).directive('bsDropdown', ['$window', '$sce', '$dropdown', function (e, t, n) {
		return {
			restrict: 'EAC', scope: !0, link: function (e, t, a, o) {
				var i = {scope: e};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'placement', 'container', 'delay', 'trigger', 'keyboard', 'html', 'animation', 'id'], function (e) {
					angular.isDefined(a[e]) && (i[e] = a[e])
				});
				var r = /^(false|0|)$/i;
				angular.forEach(['html', 'container'], function (e) {
					angular.isDefined(a[e]) && r.test(a[e]) && (i[e] = !1)
				}), a.bsDropdown && e.$watch(a.bsDropdown, function (t, n) {
					e.content = t
				}, !0), a.bsShow && e.$watch(a.bsShow, function (e, t) {
					s && angular.isDefined(e) && (angular.isString(e) && (e = !!e.match(/true|,?(dropdown),?/i)), e === !0 ? s.show() : s.hide())
				});
				var s = n(t, i);
				e.$on('$destroy', function () {
					s && s.destroy(), i = null, s = null
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.datepicker', ['mgcrea.ngStrap.helpers.dateParser', 'mgcrea.ngStrap.helpers.dateFormatter', 'mgcrea.ngStrap.tooltip']).provider('$datepicker', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			prefixClass: 'datepicker',
			placement: 'bottom-left',
			templateUrl: 'datepicker/datepicker.tpl.html',
			trigger: 'focus',
			container: !1,
			keyboard: !0,
			html: !1,
			delay: 0,
			useNative: !1,
			dateType: 'date',
			dateFormat: 'shortDate',
			timezone: null,
			modelDateFormat: null,
			dayFormat: 'dd',
			monthFormat: 'MMM',
			yearFormat: 'yyyy',
			monthTitleFormat: 'MMMM yyyy',
			yearTitleFormat: 'yyyy',
			strictFormat: !1,
			autoclose: !1,
			minDate: -(1 / 0),
			maxDate: +(1 / 0),
			startView: 0,
			minView: 0,
			startWeek: 0,
			daysOfWeekDisabled: '',
			iconLeft: 'glyphicon glyphicon-chevron-left',
			iconRight: 'glyphicon glyphicon-chevron-right'
		};
		this.$get = ['$window', '$document', '$rootScope', '$sce', '$dateFormatter', 'datepickerViews', '$tooltip', '$timeout', function (t, n, a, o, i, r, s, l) {
			function u(t, n, a) {
				function o(e) {
					e.selected = u.$isSelected(e.date)
				}

				function i() {
					t[0].focus()
				}

				var u = s(t, angular.extend({}, e, a)), f = a.scope, p = u.$options, g = u.$scope;
				p.startView && (p.startView -= p.minView);
				var m = r(u);
				u.$views = m.views;
				var $ = m.viewDate;
				g.$mode = p.startView, g.$iconLeft = p.iconLeft, g.$iconRight = p.iconRight;
				var h = u.$views[g.$mode];
				g.$select = function (e) {
					u.select(e)
				}, g.$selectPane = function (e) {
					u.$selectPane(e)
				}, g.$toggleMode = function () {
					u.setMode((g.$mode + 1) % u.$views.length)
				}, u.update = function (e) {
					angular.isDate(e) && !isNaN(e.getTime()) && (u.$date = e, h.update.call(h, e)), u.$build(!0)
				}, u.updateDisabledDates = function (e) {
					p.disabledDateRanges = e;
					for (var t = 0, n = g.rows.length; n > t; t++)angular.forEach(g.rows[t], u.$setDisabledEl)
				}, u.select = function (e, t) {
					angular.isDate(n.$dateValue) || (n.$dateValue = new Date(e)), !g.$mode || t ? (n.$setViewValue(angular.copy(e)), n.$render(), p.autoclose && !t && l(function () {
						u.hide(!0)
					})) : (angular.extend($, {
						year: e.getFullYear(),
						month: e.getMonth(),
						date: e.getDate()
					}), u.setMode(g.$mode - 1), u.$build())
				}, u.setMode = function (e) {
					g.$mode = e, h = u.$views[g.$mode], u.$build()
				}, u.$build = function (e) {
					e === !0 && h.built || (e !== !1 || h.built) && h.build.call(h)
				}, u.$updateSelected = function () {
					for (var e = 0, t = g.rows.length; t > e; e++)angular.forEach(g.rows[e], o)
				}, u.$isSelected = function (e) {
					return h.isSelected(e)
				}, u.$setDisabledEl = function (e) {
					e.disabled = h.isDisabled(e.date)
				}, u.$selectPane = function (e) {
					var t = h.steps, n = new Date(Date.UTC($.year + (t.year || 0) * e, $.month + (t.month || 0) * e, 1));
					angular.extend($, {
						year: n.getUTCFullYear(),
						month: n.getUTCMonth(),
						date: n.getUTCDate()
					}), u.$build()
				}, u.$onMouseDown = function (e) {
					if (e.preventDefault(), e.stopPropagation(), d) {
						var t = angular.element(e.target);
						'button' !== t[0].nodeName.toLowerCase() && (t = t.parent()), t.triggerHandler('click')
					}
				}, u.$onKeyDown = function (e) {
					if (/(38|37|39|40|13)/.test(e.keyCode) && !e.shiftKey && !e.altKey) {
						if (e.preventDefault(), e.stopPropagation(), 13 === e.keyCode)return g.$mode ? g.$apply(function () {
							u.setMode(g.$mode - 1)
						}) : u.hide(!0);
						h.onKeyDown(e), f.$digest()
					}
				};
				var v = u.init;
				u.init = function () {
					return c && p.useNative ? (t.prop('type', 'date'), void t.css('-webkit-appearance', 'textfield')) : (d && (t.prop('type', 'text'), t.attr('readonly', 'true'), t.on('click', i)), void v())
				};
				var w = u.destroy;
				u.destroy = function () {
					c && p.useNative && t.off('click', i), w()
				};
				var y = u.show;
				u.show = function () {
					!d && t.attr('readonly') || t.attr('disabled') || (y(), l(function () {
						u.$isShown && (u.$element.on(d ? 'touchstart' : 'mousedown', u.$onMouseDown), p.keyboard && t.on('keydown', u.$onKeyDown))
					}, 0, !1))
				};
				var b = u.hide;
				return u.hide = function (e) {
					u.$isShown && (u.$element.off(d ? 'touchstart' : 'mousedown', u.$onMouseDown), p.keyboard && t.off('keydown', u.$onKeyDown), b(e))
				}, u
			}

			var c = (angular.element(t.document.body), /(ip(a|o)d|iphone|android)/gi.test(t.navigator.userAgent)), d = 'createTouch'in t.document && c;
			return e.lang || (e.lang = i.getDefaultLocale()), u.defaults = e, u
		}]
	}).directive('bsDatepicker', ['$window', '$parse', '$q', '$dateFormatter', '$dateParser', '$datepicker', function (e, t, n, a, o, i) {
		var r = (i.defaults, /(ip(a|o)d|iphone|android)/gi.test(e.navigator.userAgent));
		return {
			restrict: 'EAC',
			require: 'ngModel',
			link: function (e, t, n, s) {
				function l(e) {
					return e && e.length ? e : null
				}

				function u(e) {
					if (angular.isDate(e)) {
						var t = isNaN(p.$options.minDate) || e.getTime() >= p.$options.minDate, n = isNaN(p.$options.maxDate) || e.getTime() <= p.$options.maxDate, a = t && n;
						s.$setValidity('date', a), s.$setValidity('min', t), s.$setValidity('max', n), a && (s.$dateValue = e)
					}
				}

				function c() {
					return !s.$dateValue || isNaN(s.$dateValue.getTime()) ? '' : m(s.$dateValue, d.dateFormat)
				}

				var d = {scope: e};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'placement', 'container', 'delay', 'trigger', 'html', 'animation', 'autoclose', 'dateType', 'dateFormat', 'timezone', 'modelDateFormat', 'dayFormat', 'strictFormat', 'startWeek', 'startDate', 'useNative', 'lang', 'startView', 'minView', 'iconLeft', 'iconRight', 'daysOfWeekDisabled', 'id', 'prefixClass', 'prefixEvent'], function (e) {
					angular.isDefined(n[e]) && (d[e] = n[e])
				});
				var f = /^(false|0|)$/i;
				angular.forEach(['html', 'container', 'autoclose', 'useNative'], function (e) {
					angular.isDefined(n[e]) && f.test(n[e]) && (d[e] = !1)
				}), n.bsShow && e.$watch(n.bsShow, function (e, t) {
					p && angular.isDefined(e) && (angular.isString(e) && (e = !!e.match(/true|,?(datepicker),?/i)), e === !0 ? p.show() : p.hide())
				});
				var p = i(t, s, d);
				d = p.$options, r && d.useNative && (d.dateFormat = 'yyyy-MM-dd');
				var g = d.lang, m = function (e, t) {
					return a.formatDate(e, t, g)
				}, $ = o({
					format: d.dateFormat,
					lang: g,
					strict: d.strictFormat
				});
				angular.forEach(['minDate', 'maxDate'], function (e) {
					angular.isDefined(n[e]) && n.$observe(e, function (t) {
						p.$options[e] = $.getDateForAttribute(e, t), !isNaN(p.$options[e]) && p.$build(!1), u(s.$dateValue)
					})
				}), e.$watch(n.ngModel, function (e, t) {
					p.update(s.$dateValue)
				}, !0), angular.isDefined(n.disabledDates) && e.$watch(n.disabledDates, function (e, t) {
					e = l(e), t = l(t), e && p.updateDisabledDates(e)
				}), s.$parsers.unshift(function (e) {
					var t;
					if (!e)return s.$setValidity('date', !0), null;
					var n = $.parse(e, s.$dateValue);
					return !n || isNaN(n.getTime()) ? void s.$setValidity('date', !1) : (u(n), 'string' === d.dateType ? (t = $.timezoneOffsetAdjust(n, d.timezone, !0), m(t, d.modelDateFormat || d.dateFormat)) : (t = $.timezoneOffsetAdjust(s.$dateValue, d.timezone, !0), 'number' === d.dateType ? t.getTime() : 'unix' === d.dateType ? t.getTime() / 1e3 : 'iso' === d.dateType ? t.toISOString() : new Date(t)))
				}), s.$formatters.push(function (e) {
					var t;
					return t = angular.isUndefined(e) || null === e ? NaN : angular.isDate(e) ? e : 'string' === d.dateType ? $.parse(e, null, d.modelDateFormat) : 'unix' === d.dateType ? new Date(1e3 * e) : new Date(e), s.$dateValue = $.timezoneOffsetAdjust(t, d.timezone), c()
				}), s.$render = function () {
					t.val(c())
				}, e.$on('$destroy', function () {
					p && p.destroy(), d = null, p = null
				})
			}
		}
	}]).provider('datepickerViews', function () {
		function e(e, t) {
			for (var n = []; e.length > 0;)n.push(e.splice(0, t));
			return n
		}

		function t(e, t) {
			return (e % t + t) % t
		}

		this.defaults = {dayFormat: 'dd', daySplit: 7};
		this.$get = ['$dateFormatter', '$dateParser', '$sce', function (n, a, o) {
			return function (i) {
				var r = i.$scope, s = i.$options, l = s.lang, u = function (e, t) {
					return n.formatDate(e, t, l)
				}, c = a({
					format: s.dateFormat,
					lang: l,
					strict: s.strictFormat
				}), d = n.weekdaysShort(l), f = d.slice(s.startWeek).concat(d.slice(0, s.startWeek)), p = o.trustAsHtml('<th class="dow text-center">' + f.join('</th><th class="dow text-center">') + '</th>'), g = i.$date || (s.startDate ? c.getDateForAttribute('startDate', s.startDate) : new Date), m = {
					year: g.getFullYear(),
					month: g.getMonth(),
					date: g.getDate()
				}, $ = [{
					format: s.dayFormat,
					split: 7,
					steps: {month: 1},
					update: function (e, t) {
						!this.built || t || e.getFullYear() !== m.year || e.getMonth() !== m.month ? (angular.extend(m, {
							year: i.$date.getFullYear(),
							month: i.$date.getMonth(),
							date: i.$date.getDate()
						}), i.$build()) : (e.getDate() !== m.date || 1 === e.getDate()) && (m.date = i.$date.getDate(), i.$updateSelected())
					},
					build: function () {
						var n = new Date(m.year, m.month, 1), a = n.getTimezoneOffset(), o = new Date(+n - 864e5 * t(n.getDay() - s.startWeek, 7)), l = o.getTimezoneOffset(), d = c.timezoneOffsetAdjust(new Date, s.timezone).toDateString();
						l !== a && (o = new Date(+o + 6e4 * (l - a)));
						for (var f, g = [], $ = 0; 42 > $; $++)f = c.daylightSavingAdjust(new Date(o.getFullYear(), o.getMonth(), o.getDate() + $)), g.push({
							date: f,
							isToday: f.toDateString() === d,
							label: u(f, this.format),
							selected: i.$date && this.isSelected(f),
							muted: f.getMonth() !== m.month,
							disabled: this.isDisabled(f)
						});
						r.title = u(n, s.monthTitleFormat), r.showLabels = !0, r.labels = p, r.rows = e(g, this.split), this.built = !0
					},
					isSelected: function (e) {
						return i.$date && e.getFullYear() === i.$date.getFullYear() && e.getMonth() === i.$date.getMonth() && e.getDate() === i.$date.getDate()
					},
					isDisabled: function (e) {
						var t = e.getTime();
						if (t < s.minDate || t > s.maxDate)return !0;
						if (-1 !== s.daysOfWeekDisabled.indexOf(e.getDay()))return !0;
						if (s.disabledDateRanges)for (var n = 0; n < s.disabledDateRanges.length; n++)if (t >= s.disabledDateRanges[n].start && t <= s.disabledDateRanges[n].end)return !0;
						return !1
					},
					onKeyDown: function (e) {
						if (i.$date) {
							var t, n = i.$date.getTime();
							37 === e.keyCode ? t = new Date(n - 864e5) : 38 === e.keyCode ? t = new Date(n - 6048e5) : 39 === e.keyCode ? t = new Date(n + 864e5) : 40 === e.keyCode && (t = new Date(n + 6048e5)), this.isDisabled(t) || i.select(t, !0)
						}
					}
				}, {
					name: 'month',
					format: s.monthFormat,
					split: 4,
					steps: {year: 1},
					update: function (e, t) {
						this.built && e.getFullYear() === m.year ? e.getMonth() !== m.month && (angular.extend(m, {
							month: i.$date.getMonth(),
							date: i.$date.getDate()
						}), i.$updateSelected()) : (angular.extend(m, {
							year: i.$date.getFullYear(),
							month: i.$date.getMonth(),
							date: i.$date.getDate()
						}), i.$build())
					},
					build: function () {
						for (var t, n = (new Date(m.year, 0, 1), []), a = 0; 12 > a; a++)t = new Date(m.year, a, 1),
							n.push({
								date: t,
								label: u(t, this.format),
								selected: i.$isSelected(t),
								disabled: this.isDisabled(t)
							});
						r.title = u(t, s.yearTitleFormat), r.showLabels = !1, r.rows = e(n, this.split), this.built = !0
					},
					isSelected: function (e) {
						return i.$date && e.getFullYear() === i.$date.getFullYear() && e.getMonth() === i.$date.getMonth()
					},
					isDisabled: function (e) {
						var t = +new Date(e.getFullYear(), e.getMonth() + 1, 0);
						return t < s.minDate || e.getTime() > s.maxDate
					},
					onKeyDown: function (e) {
						if (i.$date) {
							var t = i.$date.getMonth(), n = new Date(i.$date);
							37 === e.keyCode ? n.setMonth(t - 1) : 38 === e.keyCode ? n.setMonth(t - 4) : 39 === e.keyCode ? n.setMonth(t + 1) : 40 === e.keyCode && n.setMonth(t + 4), this.isDisabled(n) || i.select(n, !0)
						}
					}
				}, {
					name: 'year',
					format: s.yearFormat,
					split: 4,
					steps: {year: 12},
					update: function (e, t) {
						!this.built || t || parseInt(e.getFullYear() / 20, 10) !== parseInt(m.year / 20, 10) ? (angular.extend(m, {
							year: i.$date.getFullYear(),
							month: i.$date.getMonth(),
							date: i.$date.getDate()
						}), i.$build()) : e.getFullYear() !== m.year && (angular.extend(m, {
							year: i.$date.getFullYear(),
							month: i.$date.getMonth(),
							date: i.$date.getDate()
						}), i.$updateSelected())
					},
					build: function () {
						for (var t, n = m.year - m.year % (3 * this.split), a = [], o = 0; 12 > o; o++)t = new Date(n + o, 0, 1), a.push({
							date: t,
							label: u(t, this.format),
							selected: i.$isSelected(t),
							disabled: this.isDisabled(t)
						});
						r.title = a[0].label + '-' + a[a.length - 1].label, r.showLabels = !1, r.rows = e(a, this.split), this.built = !0
					},
					isSelected: function (e) {
						return i.$date && e.getFullYear() === i.$date.getFullYear()
					},
					isDisabled: function (e) {
						var t = +new Date(e.getFullYear() + 1, 0, 0);
						return t < s.minDate || e.getTime() > s.maxDate
					},
					onKeyDown: function (e) {
						if (i.$date) {
							var t = i.$date.getFullYear(), n = new Date(i.$date);
							37 === e.keyCode ? n.setYear(t - 1) : 38 === e.keyCode ? n.setYear(t - 4) : 39 === e.keyCode ? n.setYear(t + 1) : 40 === e.keyCode && n.setYear(t + 4), this.isDisabled(n) || i.select(n, !0)
						}
					}
				}];
				return {
					views: s.minView ? Array.prototype.slice.call($, s.minView) : $,
					viewDate: m
				}
			}
		}]
	}), angular.module('mgcrea.ngStrap.collapse', []).provider('$collapse', function () {
		var e = this.defaults = {
			animation: 'am-collapse',
			disallowToggle: !1,
			activeClass: 'in',
			startCollapsed: !1,
			allowMultiple: !1
		}, t = this.controller = function (t, n, a) {
			function o(e) {
				for (var t = l.$targets.$active, n = 0; n < t.length; n++)e < t[n] && (t[n] = t[n] - 1), t[n] === l.$targets.length && (t[n] = l.$targets.length - 1)
			}

			function i(e) {
				var t = l.$targets.$active;
				return -1 === t.indexOf(e) ? !1 : !0
			}

			function r(e) {
				var t = l.$targets.$active.indexOf(e);
				-1 !== t && l.$targets.$active.splice(t, 1)
			}

			function s(e) {
				l.$options.allowMultiple || l.$targets.$active.splice(0, 1), -1 === l.$targets.$active.indexOf(e) && l.$targets.$active.push(e)
			}

			var l = this;
			l.$options = angular.copy(e), angular.forEach(['animation', 'disallowToggle', 'activeClass', 'startCollapsed', 'allowMultiple'], function (e) {
				angular.isDefined(a[e]) && (l.$options[e] = a[e])
			});
			var u = /^(false|0|)$/i;
			angular.forEach(['disallowToggle', 'startCollapsed', 'allowMultiple'], function (e) {
				angular.isDefined(a[e]) && u.test(a[e]) && (l.$options[e] = !1)
			}), l.$toggles = [], l.$targets = [], l.$viewChangeListeners = [], l.$registerToggle = function (e) {
				l.$toggles.push(e)
			}, l.$registerTarget = function (e) {
				l.$targets.push(e)
			}, l.$unregisterToggle = function (e) {
				var t = l.$toggles.indexOf(e);
				l.$toggles.splice(t, 1)
			}, l.$unregisterTarget = function (e) {
				var t = l.$targets.indexOf(e);
				l.$targets.splice(t, 1), l.$options.allowMultiple && r(e), o(t), l.$viewChangeListeners.forEach(function (e) {
					e()
				})
			}, l.$targets.$active = l.$options.startCollapsed ? [] : [0], l.$setActive = t.$setActive = function (e) {
				angular.isArray(e) ? l.$targets.$active = e : l.$options.disallowToggle ? s(e) : i(e) ? r(e) : s(e), l.$viewChangeListeners.forEach(function (e) {
					e()
				})
			}, l.$activeIndexes = function () {
				return l.$options.allowMultiple ? l.$targets.$active : 1 === l.$targets.$active.length ? l.$targets.$active[0] : -1
			}
		};
		this.$get = function () {
			var n = {};
			return n.defaults = e, n.controller = t, n
		}
	}).directive('bsCollapse', ['$window', '$animate', '$collapse', function (e, t, n) {
		n.defaults;
		return {
			require: ['?ngModel', 'bsCollapse'],
			controller: ['$scope', '$element', '$attrs', n.controller],
			link: function (e, t, n, a) {
				var o = a[0], i = a[1];
				o && (i.$viewChangeListeners.push(function () {
					o.$setViewValue(i.$activeIndexes())
				}), o.$formatters.push(function (e) {
					if (angular.isArray(e))i.$setActive(e); else {
						var t = i.$activeIndexes();
						angular.isArray(t) ? -1 === t.indexOf(1 * e) && i.$setActive(1 * e) : t !== 1 * e && i.$setActive(1 * e)
					}
					return e
				}))
			}
		}
	}]).directive('bsCollapseToggle', function () {
		return {
			require: ['^?ngModel', '^bsCollapse'],
			link: function (e, t, n, a) {
				var o = (a[0], a[1]);
				t.attr('data-toggle', 'collapse'), o.$registerToggle(t), e.$on('$destroy', function () {
					o.$unregisterToggle(t)
				}), t.on('click', function () {
					var a = n.bsCollapseToggle && 'bs-collapse-toggle' !== n.bsCollapseToggle ? n.bsCollapseToggle : o.$toggles.indexOf(t);
					o.$setActive(1 * a), e.$apply()
				})
			}
		}
	}).directive('bsCollapseTarget', ['$animate', function (e) {
		return {
			require: ['^?ngModel', '^bsCollapse'],
			link: function (t, n, a, o) {
				function i() {
					var t = r.$targets.indexOf(n), a = r.$activeIndexes(), o = 'removeClass';
					angular.isArray(a) ? -1 !== a.indexOf(t) && (o = 'addClass') : t === a && (o = 'addClass'), e[o](n, r.$options.activeClass)
				}

				var r = (o[0], o[1]);
				n.addClass('collapse'), r.$options.animation && n.addClass(r.$options.animation), r.$registerTarget(n), t.$on('$destroy', function () {
					r.$unregisterTarget(n)
				}), r.$viewChangeListeners.push(function () {
					i()
				}), i()
			}
		}
	}]), angular.module('mgcrea.ngStrap.button', []).provider('$button', function () {
		var e = this.defaults = {activeClass: 'active', toggleEvent: 'click'};
		this.$get = function () {
			return {defaults: e}
		}
	}).directive('bsCheckboxGroup', function () {
		return {
			restrict: 'A', require: 'ngModel', compile: function (e, t) {
				e.attr('data-toggle', 'buttons'), e.removeAttr('ng-model');
				var n = e[0].querySelectorAll('input[type="checkbox"]');
				angular.forEach(n, function (e) {
					var n = angular.element(e);
					n.attr('bs-checkbox', ''), n.attr('ng-model', t.ngModel + '.' + n.attr('value'))
				})
			}
		}
	}).directive('bsCheckbox', ['$button', '$$rAF', function (e, t) {
		var n = e.defaults, a = /^(true|false|\d+)$/;
		return {
			restrict: 'A', require: 'ngModel', link: function (e, o, i, r) {
				var s = n, l = 'INPUT' === o[0].nodeName, u = l ? o.parent() : o, c = angular.isDefined(i.trueValue) ? i.trueValue : !0;
				a.test(i.trueValue) && (c = e.$eval(i.trueValue));
				var d = angular.isDefined(i.falseValue) ? i.falseValue : !1;
				a.test(i.falseValue) && (d = e.$eval(i.falseValue));
				var f = 'boolean' != typeof c || 'boolean' != typeof d;
				f && (r.$parsers.push(function (e) {
					return e ? c : d
				}), r.$formatters.push(function (e) {
					return angular.equals(e, c)
				}), e.$watch(i.ngModel, function (e, t) {
					r.$render()
				})), r.$render = function () {
					var e = angular.equals(r.$modelValue, c);
					t(function () {
						l && (o[0].checked = e), u.toggleClass(s.activeClass, e)
					})
				}, o.bind(s.toggleEvent, function () {
					e.$apply(function () {
						l || r.$setViewValue(!u.hasClass('active')), f || r.$render()
					})
				})
			}
		}
	}]).directive('bsRadioGroup', function () {
		return {
			restrict: 'A', require: 'ngModel', compile: function (e, t) {
				e.attr('data-toggle', 'buttons'), e.removeAttr('ng-model');
				var n = e[0].querySelectorAll('input[type="radio"]');
				angular.forEach(n, function (e) {
					angular.element(e).attr('bs-radio', ''), angular.element(e).attr('ng-model', t.ngModel)
				})
			}
		}
	}).directive('bsRadio', ['$button', '$$rAF', function (e, t) {
		var n = e.defaults, a = /^(true|false|\d+)$/;
		return {
			restrict: 'A', require: 'ngModel', link: function (e, o, i, r) {
				var s, l = n, u = 'INPUT' === o[0].nodeName, c = u ? o.parent() : o;
				i.$observe('value', function (t) {
					s = a.test(t) ? e.$eval(t) : t, r.$render()
				}), r.$render = function () {
					var e = angular.equals(r.$modelValue, s);
					t(function () {
						u && (o[0].checked = e), c.toggleClass(l.activeClass, e)
					})
				}, o.bind(l.toggleEvent, function () {
					e.$apply(function () {
						r.$setViewValue(s), r.$render()
					})
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.aside', ['mgcrea.ngStrap.modal']).provider('$aside', function () {
		var e = this.defaults = {
			animation: 'am-fade-and-slide-right',
			prefixClass: 'aside',
			prefixEvent: 'aside',
			placement: 'right',
			templateUrl: 'aside/aside.tpl.html',
			contentTemplate: !1,
			container: !1,
			element: null,
			backdrop: !0,
			keyboard: !0,
			html: !1,
			show: !0
		};
		this.$get = ['$modal', function (t) {
			function n(n) {
				var a = {}, o = angular.extend({}, e, n);
				return a = t(o)
			}

			return n
		}]
	}).directive('bsAside', ['$window', '$sce', '$aside', function (e, t, n) {
		e.requestAnimationFrame || e.setTimeout;
		return {
			restrict: 'EAC', scope: !0, link: function (e, a, o, i) {
				var r = {scope: e, element: a, show: !1};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'contentTemplate', 'placement', 'backdrop', 'keyboard', 'html', 'container', 'animation'], function (e) {
					angular.isDefined(o[e]) && (r[e] = o[e])
				});
				var s = /^(false|0|)$/i;
				angular.forEach(['backdrop', 'keyboard', 'html', 'container'], function (e) {
					angular.isDefined(o[e]) && s.test(o[e]) && (r[e] = !1)
				}), angular.forEach(['title', 'content'], function (n) {
					o[n] && o.$observe(n, function (a, o) {
						e[n] = t.trustAsHtml(a)
					})
				}), o.bsAside && e.$watch(o.bsAside, function (t, n) {
					angular.isObject(t) ? angular.extend(e, t) : e.content = t
				}, !0);
				var l = n(r);
				a.on(o.trigger || 'click', l.toggle), e.$on('$destroy', function () {
					l && l.destroy(), r = null, l = null
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.alert', ['mgcrea.ngStrap.modal']).provider('$alert', function () {
		var e = this.defaults = {
			animation: 'am-fade',
			prefixClass: 'alert',
			prefixEvent: 'alert',
			placement: null,
			templateUrl: 'alert/alert.tpl.html',
			container: !1,
			element: null,
			backdrop: !1,
			keyboard: !0,
			show: !0,
			duration: !1,
			type: !1,
			dismissable: !0
		};
		this.$get = ['$modal', '$timeout', function (t, n) {
			function a(a) {
				var o = {}, i = angular.extend({}, e, a);
				o = t(i), o.$scope.dismissable = !!i.dismissable, i.type && (o.$scope.type = i.type);
				var r = o.show;
				return i.duration && (o.show = function () {
					r(), n(function () {
						o.hide()
					}, 1e3 * i.duration)
				}), o
			}

			return a
		}]
	}).directive('bsAlert', ['$window', '$sce', '$alert', function (e, t, n) {
		e.requestAnimationFrame || e.setTimeout;
		return {
			restrict: 'EAC', scope: !0, link: function (e, a, o, i) {
				var r = {scope: e, element: a, show: !1};
				angular.forEach(['template', 'templateUrl', 'controller', 'controllerAs', 'placement', 'keyboard', 'html', 'container', 'animation', 'duration', 'dismissable'], function (e) {
					angular.isDefined(o[e]) && (r[e] = o[e])
				});
				var s = /^(false|0|)$/i;
				angular.forEach(['keyboard', 'html', 'container', 'dismissable'], function (e) {
					angular.isDefined(o[e]) && s.test(o[e]) && (r[e] = !1)
				}), e.hasOwnProperty('title') || (e.title = ''), angular.forEach(['title', 'content', 'type'], function (n) {
					o[n] && o.$observe(n, function (a, o) {
						e[n] = t.trustAsHtml(a)
					})
				}), o.bsAlert && e.$watch(o.bsAlert, function (t, n) {
					angular.isObject(t) ? angular.extend(e, t) : e.content = t
				}, !0);
				var l = n(r);
				a.on(o.trigger || 'click', l.toggle), e.$on('$destroy', function () {
					l && l.destroy(), r = null, l = null
				})
			}
		}
	}]), angular.module('mgcrea.ngStrap.affix', ['mgcrea.ngStrap.helpers.dimensions', 'mgcrea.ngStrap.helpers.debounce']).provider('$affix', function () {
		var e = this.defaults = {offsetTop: 'auto', inlineStyles: !0};
		this.$get = ['$window', 'debounce', 'dimensions', function (t, n, a) {
			function o(o, s) {
				function l(e, t, n) {
					var a = u(), o = c();
					return v >= a ? 'top' : null !== e && a + e <= t.top ? 'middle' : null !== w && t.top + n + $ >= o - w ? 'bottom' : 'middle'
				}

				function u() {
					return p[0] === t ? t.pageYOffset : p[0].scrollTop
				}

				function c() {
					return p[0] === t ? t.document.body.scrollHeight : p[0].scrollHeight
				}

				var d = {}, f = angular.extend({}, e, s), p = f.target, g = 'affix affix-top affix-bottom', m = !1, $ = 0, h = 0, v = 0, w = 0, y = null, b = null, D = o.parent();
				if (f.offsetParent)if (f.offsetParent.match(/^\d+$/))for (var k = 0; k < 1 * f.offsetParent - 1; k++)D = D.parent(); else D = angular.element(f.offsetParent);
				return d.init = function () {
					this.$parseOffsets(), h = a.offset(o[0]).top + $, m = !o[0].style.width, p.on('scroll', this.checkPosition), p.on('click', this.checkPositionWithEventLoop), r.on('resize', this.$debouncedOnResize), this.checkPosition(), this.checkPositionWithEventLoop()
				}, d.destroy = function () {
					p.off('scroll', this.checkPosition), p.off('click', this.checkPositionWithEventLoop), r.off('resize', this.$debouncedOnResize)
				}, d.checkPositionWithEventLoop = function () {
					setTimeout(d.checkPosition, 1)
				}, d.checkPosition = function () {
					var e = u(), t = a.offset(o[0]), n = a.height(o[0]), r = l(b, t, n);
					y !== r && (y = r, 'top' === r ? (b = null, m && o.css('width', ''), f.inlineStyles && (o.css('position', f.offsetParent ? '' : 'relative'), o.css('top', ''))) : 'bottom' === r ? (b = f.offsetUnpin ? -(1 * f.offsetUnpin) : t.top - e, m && o.css('width', ''), f.inlineStyles && (o.css('position', f.offsetParent ? '' : 'relative'), o.css('top', f.offsetParent ? '' : i[0].offsetHeight - w - n - h + 'px'))) : (b = null, m && o.css('width', o[0].offsetWidth + 'px'), f.inlineStyles && (o.css('position', 'fixed'), o.css('top', $ + 'px'))), o.removeClass(g).addClass('affix' + ('middle' !== r ? '-' + r : '')))
				}, d.$onResize = function () {
					d.$parseOffsets(), d.checkPosition()
				}, d.$debouncedOnResize = n(d.$onResize, 50), d.$parseOffsets = function () {
					var e = o.css('position');
					f.inlineStyles && o.css('position', f.offsetParent ? '' : 'relative'), f.offsetTop && ('auto' === f.offsetTop && (f.offsetTop = '+0'), f.offsetTop.match(/^[-+]\d+$/) ? ($ = 1 * -f.offsetTop, v = f.offsetParent ? a.offset(D[0]).top + 1 * f.offsetTop : a.offset(o[0]).top - a.css(o[0], 'marginTop', !0) + 1 * f.offsetTop) : v = 1 * f.offsetTop), f.offsetBottom && (w = f.offsetParent && f.offsetBottom.match(/^[-+]\d+$/) ? c() - (a.offset(D[0]).top + a.height(D[0])) + 1 * f.offsetBottom + 1 : 1 * f.offsetBottom), f.inlineStyles && o.css('position', e)
				}, d.init(), d
			}

			var i = angular.element(t.document.body), r = angular.element(t);
			return o
		}]
	}).directive('bsAffix', ['$affix', '$window', function (e, t) {
		return {
			restrict: 'EAC',
			require: '^?bsAffixTarget',
			link: function (n, a, o, i) {
				var r = {scope: n, target: i ? i.$element : angular.element(t)};
				angular.forEach(['offsetTop', 'offsetBottom', 'offsetParent', 'offsetUnpin', 'inlineStyles'], function (e) {
					if (angular.isDefined(o[e])) {
						var t = o[e];
						/true/i.test(t) && (t = !0), /false/i.test(t) && (t = !1), r[e] = t
					}
				});
				var s = e(a, r);
				n.$on('$destroy', function () {
					s && s.destroy(), r = null, s = null
				})
			}
		}
	}]).directive('bsAffixTarget', function () {
		return {
			controller: ['$element', function (e) {
				this.$element = e
			}]
		}
	}), angular.module('mgcrea.ngStrap', ['mgcrea.ngStrap.modal', 'mgcrea.ngStrap.aside', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.button', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.datepicker', 'mgcrea.ngStrap.timepicker', 'mgcrea.ngStrap.navbar', 'mgcrea.ngStrap.tooltip', 'mgcrea.ngStrap.popover', 'mgcrea.ngStrap.dropdown', 'mgcrea.ngStrap.typeahead', 'mgcrea.ngStrap.scrollspy', 'mgcrea.ngStrap.affix', 'mgcrea.ngStrap.tab', 'mgcrea.ngStrap.collapse'])
}(window, document);
//# sourceMappingURL=angular-strap.min.js.map