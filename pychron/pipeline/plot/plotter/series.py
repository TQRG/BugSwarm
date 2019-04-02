# ===============================================================================
# Copyright 2013 Jake Ross
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ===============================================================================

# ============= enthought library imports =======================
import time

from chaco.array_data_source import ArrayDataSource
from chaco.scales.time_scale import CalendarScaleSystem
from chaco.scales_tick_generator import ScalesTickGenerator
from traits.api import Array


# ============= standard library imports ========================
from numpy import array, Inf
# ============= local library imports  ==========================
from pychron.experiment.utilities.identifier import ANALYSIS_MAPPING_INTS
from pychron.pipeline.plot.plotter.arar_figure import BaseArArFigure
from pychron.pipeline.plot.plotter.ticks import TICKS

N = 500


class BaseSeries(BaseArArFigure):
    xs = Array

    def max_x(self, *args):
        if len(self.xs):
            return max(self.xs)
        return -Inf

    def min_x(self, *args):
        if len(self.xs):
            return min(self.xs)
        return Inf

    def mean_x(self, *args):
        if len(self.xs):
            return self.xs.mean()
        return 0

    def _get_xs(self, plots, ans, tzero=None):

        xs = array([ai.timestamp for ai in ans])
        px = plots[0]
        if tzero is None:
            if px.normalize == 'now':
                tzero = time.time()
            else:
                tzero = xs[-1]

        xs -= tzero
        if not px.use_time_axis:
            xs /= 3600.
        else:
            self.graph.convert_index_func = lambda x: '{:0.2f} hrs'.format(x / 3600.)

        return xs

    def _handle_limits(self):
        self.graph.refresh()


class Series(BaseSeries):
    _omit_key = 'omit_series'

    def _has_attr(self, name):
        a = name in ('AnalysisType', 'Peak Center')
        if not a:
            if self.sorted_analyses:
                ai = self.sorted_analyses[0]
                a = bool(ai.get_value(name))
        return a

    def build(self, plots):

        graph = self.graph
        plots = (pp for pp in plots if self._has_attr(pp.name))
        for i, po in enumerate(plots):

            p = graph.new_plot(
                # padding=self.padding,
                ytitle=po.name,
                xtitle='Time (hrs)')
            # self._add_limit_tool(p, 'y')

            if po.name == 'AnalysisType':
                from pychron.pipeline.plot.plotter.ticks import tick_formatter, StaticTickGenerator

                p.y_axis.tick_label_formatter = tick_formatter
                p.y_axis.tick_generator = StaticTickGenerator()
                # p.y_axis.tick_label_rotate_angle = 45
                graph.set_y_limits(-0.5, len(TICKS) - 0.5, plotid=i)
                # graph.set_y_limits(min_=-1, max_=7, plotid=i)

            # p.value_scale = po.scale
            # p.padding_left = 75
            p.value_range.tight_bounds = False
            self._setup_plot(i, p, po)

    def plot(self, plots, legend=None):
        """
            plot data on plots
        """

        omits = self._get_omitted_by_tag(self.sorted_analyses)
        for o in omits:
            self.sorted_analyses[o].set_temp_status('omit')
        graph = self.graph

        if plots:
            self.xs = self._get_xs(plots, self.sorted_analyses)
            # with graph.no_regression(refresh=True):
            # plots = [po for po in plots if po.use]
            for i, po in enumerate(plots):
                self._plot_series(po, i, omits)

            self.xmi, self.xma = self.min_x(), self.max_x()
            self.xpad = '0.1'

    def _plot_series(self, po, pid, omits):
        graph = self.graph
        try:
            if po.name == 'AnalysisType':
                from pychron.pipeline.plot.plotter.ticks import analysis_type_formatter

                ys = list(self._unpack_attr(po.name))
                kw = dict(y=ys, colors=ys, type='cmap_scatter',
                          fit='',
                          color_map_name='gist_rainbow')
                yerr = None
                value_format = analysis_type_formatter
                set_ylimits = False

            else:
                set_ylimits = True
                value_format = None
                ys = array([ai.nominal_value for ai in self._unpack_attr(po.name)])
                yerr = array([ai.std_dev for ai in self._unpack_attr(po.name)])
                kw = dict(y=ys, yerror=yerr, type='scatter',
                          fit='{}_{}'.format(po.fit, po.error_type),
                          filter_outliers_dict=po.filter_outliers_dict)

            # print ys
            n = [ai.record_id for ai in self.sorted_analyses]
            args = graph.new_series(x=self.xs,
                                    display_index=ArrayDataSource(data=n),
                                    plotid=pid,
                                    add_inspector=False,
                                    marker=po.marker,
                                    marker_size=po.marker_size,
                                    **kw)
            if len(args) == 2:
                scatter, p = args
            else:
                p, scatter, l = args

            sel = scatter.index.metadata.get('selections', [])
            sel += omits
            scatter.index.metadata['selections'] = list(set(sel))

            def af(i, x, y, analysis):
                return ('Run Date: {}'.format(analysis.rundate.strftime('%m-%d-%Y %H:%M')),
                        'Rel. Time: {:0.4f}'.format(x))

            self._add_scatter_inspector(scatter,
                                        additional_info=af,
                                        value_format=value_format)

            if po.use_time_axis:
                p.x_axis.tick_generator = ScalesTickGenerator(scale=CalendarScaleSystem())

            # p.value_scale = po.scale
            end_caps = True
            if po.y_error and yerr is not None:
                self._add_error_bars(scatter, yerr, 'y', 2, end_caps, visible=True)

            if set_ylimits:
                mi, mx = min(ys - 2 * yerr), max(ys + 2 * yerr)
                graph.set_y_limits(min_=mi, max_=mx, pad='0.1', plotid=pid)

        except (KeyError, ZeroDivisionError, AttributeError), e:
            import traceback

            traceback.print_exc()
            print 'Series', e

    def _unpack_attr(self, attr):
        # if attr.endswith('bs'):
        #     # f=lambda x: x.baseline.uvalue
        #     return (ai.get_baseline(attr).uvalue for ai in self.sorted_analyses)
        if attr == 'AnalysisType':
            # amap={'unknown':1, 'blank_unknown':2, 'blank_air':3, 'blank_cocktail':4}
            f = lambda x: ANALYSIS_MAPPING_INTS[x] if x in ANALYSIS_MAPPING_INTS else -1
            return (f(ai.analysis_type) for ai in self.sorted_analyses)
        elif attr == 'Peak Center':
            return (ai.peak_center for ai in self.sorted_analyses)
        else:
            return super(Series, self)._unpack_attr(attr)

            # def update_graph_metadata(self, obj, name, old, new):
            #     sorted_ans = self.sorted_analyses
            #     if obj:
            #         hover = obj.metadata.get('hover')
            #         if hover:
            #             hoverid = hover[0]
            #             try:
            #                 self.selected_analysis = sorted_ans[hoverid]
            #
            #             except IndexError, e:
            #                 print 'asaaaaa', e
            #                 return
            #         else:
            #             self.selected_analysis = None
            #
            #         sel = self._filter_metadata_changes(obj, lambda x: x, sorted_ans)
            #         print 'ssss', sel
            # self._set_renderer_selection()
            # self._set_selected(sorted_ans, sel)
            # set the temp_status for all the analyses
            # for i, a in enumerate(sorted_ans):
            #    a.temp_status = 1 if i in sel else 0
            # else:
            # sel = [i for i, a in enumerate(sorted_ans)
            #            if a.temp_status]
            # sel = self._get_omitted(sorted_ans, omit='omit_ideo')
            # print 'update graph meta'
            # self._rebuild_ideo(sel)
            # self.

    def update_graph_metadata(self, obj, name, old, new):
        # print obj, name, old,new
        sorted_ans = self.sorted_analyses
        if obj:
            self._filter_metadata_changes(obj, sorted_ans)
# ===============================================================================
# plotters
# ===============================================================================

# ===============================================================================
# overlays
# ===============================================================================

# ===============================================================================
# utils
# ===============================================================================

# ===============================================================================
# labels
# ===============================================================================

# ============= EOF =============================================
