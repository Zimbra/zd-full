/*
 * 
 */

#ifndef CFG_H
#define CFG_H

#include <string>
#include <map>

using namespace std;

class Config {
public:
    Config() {};
    ~Config() {};

    bool Load(string &cfgfile);
    string &Get(const char *key);
    string &Get(string &key) { return Get(key.c_str()); }

protected:
    typedef map<string, string> CfgMap;

    CfgMap cfg;
    CfgMap vars;

    void Expand(string &val);

#ifdef _DEBUG
public:
    void Dump(ostream &s);
#endif

};

#endif