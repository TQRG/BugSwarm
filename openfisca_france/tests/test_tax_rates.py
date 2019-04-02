# -*- coding: utf-8 -*-

from openfisca_core.rates import average_rate, marginal_rate
from openfisca_france.tests import base


def test_average_tax_rate():
    year = 2013
    simulation = base.tax_benefit_system.new_scenario().init_single_entity(
        axes = [
            dict(
                count = 100,
                name = 'salaire_imposable',
                max = 24000,
                min = 0,
                ),
            ],
        period = year,
        parent1 = dict(age_en_mois = 40 * 12 + 6),
        ).new_simulation()  # Remove debug = True, because logging is too slow.
    assert (average_rate(
        target = simulation.calculate('revdisp'),
        varying = simulation.calculate('revdisp'),
        ) == 0).all()


def test_marginal_tax_rate():
    year = 2013
    simulation = base.tax_benefit_system.new_scenario().init_single_entity(
        axes = [
            dict(
                count = 10000,
                name = 'salaire_imposable',
                max = 1000000,
                min = 0,
                ),
            ],
        period = year,
        parent1 = dict(age_en_mois = 40 * 12 + 6),
        ).new_simulation()  # Remove debug = True, because logging is too slow.
    assert (marginal_rate(
        target = simulation.calculate('revdisp'),
        varying = simulation.calculate('revdisp'),
        ) == 0).all()


if __name__ == '__main__':
    import logging
    import sys
    logging.basicConfig(level = logging.ERROR, stream = sys.stdout)
    test_marginal_tax_rate()
    test_average_tax_rate()
