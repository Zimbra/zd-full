# a',
            'gzip-1.3.5',
            'cdrtools-2.01',
            'perl-5.8.8',
         ]
         path = ['/build/toolchain/lin32/%s/bin' % p for p in pkgs]
      path += [env['PATH']]
      env['PATH'] = os.pathsep.join(path)

      OVF_OFFICIAL_KEY = 1
      if self.options.get(ALLOW_OFFICIAL_KEY) and OVF_OFFICIAL_KEY:
         self.log.debug('Turning on official OVF signing.')
         env['OVF_OFFICIAL_KEY'] = '1'

      env['CREATE_OSS_TGZ'] = '1'

      return env

