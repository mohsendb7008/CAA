import os, pandas

if __name__ == "__main__":
    data = pandas.read_csv('codejam2019.csv')
    for row in data.iterrows():
        if not row[1]['file'].endswith('.CPP'):
            continue
        if not os.path.exists(row[1]['username']):
            os.mkdir(row[1]['username'])
        file = open('%s/%s' % (row[1]['username'], row[1]['file']), 'w')
        file.write(row[1]['flines'])
        file.close()
		