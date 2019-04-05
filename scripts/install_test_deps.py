#! /usr/bin/python

import subprocess
import sys
import os

_PYPY = hasattr(sys, "pypy_version_info")

_BIN_PATH = os.path.dirname(sys.executable)

if __name__ == '__main__':
    deps = [
        "coverage",
        "coveralls",
        "munch",
        "pytest",
        "pytest-xdist",
        "pytest-capturelog",
        "pytest-cov",
        "pytest-timeout",
        "pyforge",
    ]
    if not _PYPY:
        deps.append("lxml<3.6.3")
        if sys.version_info < (2, 7):
            deps.append("pylint<1.4.0")
        else:
            deps.append("pylint>=1.0.0")

    if sys.version_info < (2, 7):
        deps.append("unittest2")

    subprocess.check_call("{0} install {1}".format(
        os.path.join(_BIN_PATH, "pip"),
        " ".join(repr(dep) for dep in deps)), shell=True)
