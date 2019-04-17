# Do not collect any tests in externals
def pytest_ignore_collect(path, config):
    return True
    
