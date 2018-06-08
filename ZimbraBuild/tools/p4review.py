#!/usr/bin/python
# 
# 
# 
# $Id: //depot/r04.2/p4-tools/server/p4review.py#1 $
#
# Perforce review daemon
#
# This script emails descriptions of new changelists and/or new or modified
# jobs to users who have expressed an interest in them.  Users express
# interest in reviewing changes and/or jobs by setting the "Reviews:" field
# on their user form (see "p4 help user").  Users are notified of changes
# if they review any file involved in that change. Users are notified of
# job updates if they review "//depot/jobs". (This value is configurable
# - see the <jobpath> configuration variable, below).
#
# If run directly with the <repeat> configuration variable = 1, the script
# will sleep for "sleeptime" seconds and then run again.  On UNIX you can
# run the script from cron by setting <repeat> = 0 and adding the following
# line to the cron table with "crontab -e:"
#
#        * * * * * /path/to/p4review.py
#
# This will run the script every minute.  Note that if you use cron you
# should be sure that the script will complete within the time allotted.
#
# The CONFIGURATION VARIABLES below should be examined and in some
# cases changed.
#
#
# Common errors and debugging tips:
#
# -> Error: "command not found" (Windows) or "name: not found" (UNIX) errors.
#
#     - On Windows, check that "p4" is on your PATH or set:
#       p4='"c:/program files/perforce/p4"' (or to the appropriate path).
#       (NOTE the use of " inside the string to prevent interpretation of
#       the command as "run c:/program with arguments files/perforce/p4...")
#
#     - On UNIX, set p4='/usr/local/bin/p4' (or to the appropriate path)
#
# -> Error: "You don't have permission for this operation"
#
#     - Check that the user you set os.environ['P4USER'] to (see below)
#       has "review" or "super" permission via "p4 protect".
#       This user should be able to run "p4 -u username counter test 42"
#       (this sets the value of a counter named "test" to 42)
#
# -> Error: "Unable to connect to SMTP host"
#
#     - check that the mailhost is set correctly - try "telnet mailhost 25"
#       and see if you connect to an SMTP server.  Type "quit" to exit
#
# -> Problem: The review daemon seems to run but you don't get email
#
#     - check the output of "p4 counters" - you should see a counter named
#       "review"
#     - check the output of "p4 reviews -c changenum" for a recent change;
#       if no one is reviewing the change then no email will be sent.
#       Check the setting of "Reviews:" on the user form with "p4 user"
#     - check that your email address is set correctly on the user form
#       (run "p4 reviews" to see email addresses for all reviewers,
#       run "p4 user" to set email address)
#
# -> Problem: Multiple job notifications are sent
#
#     - the script should be run on the same machine as the Perforce
#       server; otherwise time differences between the two machines can
#       cause problems. This is because the job review mechanism uses a
#       timestamp. The change review mechanism uses change numbers, so
#       it's not affected by this problem.


import sys, os, string, re, time, smtplib, traceback

##########################################################################
#############                                                    #########
#############      CONFIGURATION VARIABLES: CHANGE AS NEEDED     #########
#############                                                    #########
#
# In particular, be sure to set/check the first seven variables:
# <debug>, <administrator>, <mailhost>, <P4PORT>, <P4USER>, <p4>,
# and <repeat>. The script is set to run initially in "debug" mode -
# once it is configured and seems to be doing the right thing, set
# <debug> to 0.

debug = 0
    # If <debug> is true then messages go to stdout
    # and email is not actually sent to users. Instead,
    # all messages go to <administrator>, or no email
    # is sent at all if <administrator> = None. Setting
    # <debug> to larger values creates more output.

administrator = 'marcmac@zimbra.com'
    # Set this to the Perforce system administrator's
    # email address. The <administrator> will be notified
    # of problems with the script (e.g. invalid
    # email addresses for users). If <bcc_admin> is set
    # <administrator> will get a copy of all email the
    # script generates.

mailhost = 'dogfood.liquidsys.com'
    # The hostname of the machine running your local SMTP server.

os.environ['P4PORT'] = 'eric:1666'
os.environ['P4USER'] = 'review_daemon'
    # This user must have Perforce review privileges (via "p4 protect")

p4 = '/usr/local/p4/bin/p4 -c BUILD_template -P Reviewer'
    # The path of your p4 executable. You can use
    # just 'p4' if the executable is in your path.
    # NOTE: Use forward slashes EVEN ON WINDOWS,
    # since backslashes have a special meaning in Python)

repeat  = 0
    # Set to 1 to repeat every <sleeptime> seconds.
    # Set to 0 to run just once - do this if running from cron.

sleeptime = 60
    # Number of seconds to sleep between invocations
    # Irrelevant if <repeat>, above, is 0.

limit_emails = 50
    # Don't send more than this many emails of
    # each type (job and change) at a time. This is
    # a safety feature that prevents runaway email.

notify_changes = 1
notify_jobs    = 1
    # Set <notify_changes> to 0 to disable change notification completely.
    # Set <notify_jobs>  to 0 to disable job notification completely.
    # (Note that at least ONE of these should be set to true: otherwise
    # there's no reason to ever run this program!)

bcc_admin      = 0
    # Set to 1 to Bcc: ALL email to the administrator

send_to_author = 1
    # Set to 1 to CC: email to the original author of the changelist or job

reply_to_admin = 0
    # Set to 1 to set the emails Reply-To: field to the administrator

maildomain = 'zimbra.com'
    # If value is None, the script will look up the email
    # address for each Perforce user in the "p4 user"
    # data via "p4 user -o <username>". If, instead,
    # you set this variable <maildomain> to any domain
    # (like "yourcompany.com") then the review daemon
    # will assume that Perforce user <username> has
    # the email address <username>@<maildomain>.

complain_from  = 'PerforceReviewDaemon@zimbra.com'
    # The email address FROM which to send complaints to
    # the adminstrator from the "complain" function.

jobpath = '//depot/jobs'
    # Send job review mail to users reviewing <jobpath>.
    # See "p4 help user" for more information.

datefield = 'ModifiedDate'
    # A job field used to determine which jobs users are notified
    # of changes to. When the review daemon runs, subscribing users are
    # notified of any jobs with a higher value in this field then the
    # value in the "jobreview" counter -- and when job notification is
    # completed, the "jobreview' counter is updated accordingly.
    #
    # This field needs to appear in your jobspec as a "date" field
    # with persistence "always". See "p4 help jobspec" for more
    # information.

#############                                                    ##########
#############           END OF CONFIGURATION VARIABLES           ##########
#############                                                    ##########
###########################################################################

bcc_admin = bcc_admin and administrator # don't Bcc: None!
if administrator and reply_to_admin:
  replyto_line='Reply-To: '+administrator+'\n'
else:
  replyto_line=''


def complain(mailport,complaint):
  '''
  Send a plaintive message to the human looking after this script if we
  have any difficulties.  If no email address for such a human is given,
  send the complaint to stderr.
  '''
  complaint = complaint + '\n'
  if administrator:
    mailport.sendmail(complain_from,[administrator],\
      'Subject: Perforce Review Daemon Problem\n\n' + complaint)
  else:
    sys.stderr.write(complaint)


def mailit(mailport, sender, recipients, message):
  '''
  Try to mail message from sender to list of recipients using SMTP object
  mailport.  complain() if there are any problems.
  '''
  if debug:
    if not administrator:
      print 'Debug mode, no mail sent: would have sent mail ' \
            + 'from %s to %s' % (sender,recipients)
      return
    print 'Sending mail from %s to %s (normally would have sent to %s)' \
           % (sender,administrator,recipients)
    message = message + '\nIN DEBUG MODE: would normally have sent to %s' \
              % recipients
    recipients =  administrator      # for testing or initial setup
  try:
    failed = mailport.sendmail(sender, recipients, message)
  except:
    failed = string.join(apply(traceback.format_exception,sys.exc_info()),'')

  if failed:
    complain( mailport, 'The following errors occurred:\n\n' +\
               repr(failed) +\
              '\n\nwhile trying to email from\n' \
              + repr(sender) + '\nto ' \
              + repr(recipients) + '\nwith body\n\n' + message)


def set_counter(mailport,counter,value):
  if debug: print 'setting counter %s to %s' % (counter,repr(value))
  set_result = os.system('%s counter %s %s' % (p4,counter,value)) 
  if set_result !=0:
    complain(mailport,'Unable to set review counter - check user %s ' \
                       + 'has review privileges\n(use p4 protect)"' \
                       % os.environ['P4USER'])


def parse_p4_review(command,ignore_author=None):
  reviewers_email = []
  reviewers_email_and_fullname = []

  if debug>1: print 'parse_p4_review: %s' % command
  for line in os.popen(command,'r').readlines():
    if debug>1: print line
    # sample line: james <james@perforce.com> (James Strickland)
    #              user   email                fullname
    (user,email,fullname) = \
      re.match( r'^(\S+) <(\S+)> \((.+)\)$', line).groups()

    if maildomain:      # for those who don't use "p4 user" email addresses
      email= '%s@%s' % (user, maildomain)

    if user != ignore_author:
      reviewers_email.append(email)
      reviewers_email_and_fullname.append('"%s" <%s>' % (fullname,email))

  if debug>1: print reviewers_email, reviewers_email_and_fullname
  return reviewers_email,reviewers_email_and_fullname


def change_reviewers(change,ignore_author=None):
  '''
  For a given change number (given as a string!), return list of
  reviewers' email addresses, plus a list of email addresses + full names.
  If ignore_author is given then the given user will not be included
  in the lists.
  '''
  return parse_p4_review(p4 + ' reviews -c ' + change,ignore_author)


def review_changes(mailport,limit_emails=100):
  '''
  For each change which hasn't been reviewed yet send email to users
  interested in reviewing the change.  Update the "review" counter to
  reflect the last change reviewed.  Note that the number of emails sent
  is limited by the variable "limit_emails"
  '''
  if debug:
    no_one_interested=1
    current_change=int(os.popen(p4 + ' counter change').read())
    current_review=int(os.popen(p4 + ' counter review').read())
    print 'Looking for changes to review after change %d and up to %d.' \
           % (current_review, current_change)

    if current_review==0:
      print 'The review counter is set to zero.  You may want to set\
it to the last change with\n\n  %s -p %s -u %s counter review %d\n\nor \
set it to a value close to this for initial testing. (The -p and -u may \
not be necessary, but they are printed here for accuracy.)'\
% (p4,os.environ['P4PORT'],os.environ['P4USER'],current_change)
  change = None

  for line in os.popen(p4 + ' review -t review','r').readlines():
    # sample line: Change 1194 jamesst <js@perforce.com> (James Strickland)
    #              change #    author   email             fullname
    if debug: print line[:-1]
    (change,author,email,fullname) = \
      re.match( r'^Change (\d+) (\S+) <(\S+)> \(([^\)]+)\)', line).groups()

    if maildomain: # for those who don't use "p4 user" email addresses
      email= '%s@%s' % (author, maildomain)

    if send_to_author:
      (recipients,recipients_with_fullnames) = change_reviewers(change)
    else:
      (recipients,recipients_with_fullnames) = \
                              change_reviewers(change,author)

    if bcc_admin: recipients.append(administrator)

    if debug:
      if recipients:
        no_one_interested=0
        print ' users interested in this change: %s' % recipients
      else:
        print ' no users interested in this change'
    if not recipients: continue  # no one is interested

    #
    # Tim: build a more reasonable SUBJECT line
    #
    subject_text = 'P4 #' + change + ' ('
    first_line = 0;
    for desc_line in os.popen(p4 + ' describe -s ' + change, 'r').readlines() :
      if re.match('^Affected files ...', desc_line) :
        break;
      if first_line > 0:
        #        print 'desc_line is \"' + desc_line + '\"'
        if not desc_line == '\n' :
          desc_line = desc_line.rstrip();
          if not re.match('^[\s\t]+http:', desc_line) :
            desc_line = desc_line.lstrip();
            subject_text = subject_text + ' ' + desc_line
            if len(subject_text) > 60:
              break;
      first_line = first_line + 1

    # Cut it off at 78 chars in case the last-added line was really long...
    subject_text = subject_text[0:78];

    message = 'From: ' + fullname + ' <' + email + '>\n' +\
              'To: ' + string.join(recipients_with_fullnames,', ') + '\n' +\
              'X-From-Perforce: Indeed\n' +\
              'Subject: ' + subject_text + ' )\n'+\
              replyto_line +\
              '\n' +\
              os.popen(p4 + ' describe -s ' + change,'r').read()

    mailit(mailport, email, recipients, message)
    limit_emails = limit_emails - 1
    if limit_emails <= 0:
      break

  if debug and change and no_one_interested:
    print 'No users were interested in any of the changes above - perhaps \
    no one has set the Reviews: field in their client spec?  (please see \
    p4 help user").'

  # if there were change(s) reviewed in the above loop, update the counter
  if change: set_counter(mailport,'review',change)


def job_reviewers(jobname,ignore_author=None):
  '''
  For a given job, return list of reviewers' email addresses,
  plus a list of email addresses + full names.
  If ignore_author is given then the given user will not be included
  in the lists.
  '''
  return parse_p4_review(p4 + ' reviews ' + jobpath,ignore_author)
           # not the most efficient solution...


def review_jobs(mailport,limit_emails=100):
  '''
  For each job which hasn't been reviewed yet send email to users
  interested in reviewing the job.  Update the "jobreview" counter to
  reflect the last time this function was evaluated.  Note that the number
  of emails sent is limited by the variable "limit_emails" - ***currently
  this causes extra job notifications to be dropped...not optimal...
  '''
  start_time = int(os.popen(p4 + ' counter jobreview').read())
  query_time = int(time.time())
  start_time_string = \
     time.strftime('%Y/%m/%d:%H:%M:%S',time.localtime(start_time))
  query_time_string = \
     time.strftime('%Y/%m/%d:%H:%M:%S',time.localtime(query_time))
  query = \
     '%s>%s&%s<=%s' % (datefield, start_time_string, datefield,\
                       query_time_string)

  if debug:
    no_one_interested=1
    print 'Looking for jobs to review after\n%s \
          (%d seconds since 1 Jan 1970 GMT) \
          and up to\n%s (%d seconds since 1 Jan 1970 GMT).' \
          % (start_time_string, start_time, query_time_string, query_time)

  jobname=None

  for line in os.popen(p4 + ' jobs -e "' + query + '"','r').readlines():
    # sample line: job000001 on 1998/08/10 by james *closed* 'comment'
    #              jobname      date          author
    if debug: print line[:-1]
    (jobname,author) = re.match( r'^(\S+) on \S+ by (\S+)', line).groups()
    match = re.match( r'^\S+\s+<(\S+)>\s+\(([^\)]+)\)', \
    os.popen(p4 + ' users ' + author,'r').read() )
    if match:
      (email,fullname) = match.groups()
      if maildomain:     # for those who don't use "p4 user" email addresses
        email= '%s@%s' % (author, maildomain)
    else:
      email = administrator
      fullname = "Unknown user: " + author
      complain(mailport,'Unkown user %s found in job %s' % (author,jobname))

    if send_to_author:
      (recipients,recipients_with_fullnames) = job_reviewers(jobname)
    else:
      (recipients,recipients_with_fullnames) = job_reviewers(jobname,author)

    if bcc_admin: recipients.append(administrator)

    if debug:
      if recipients:
        no_one_interested=0
        print ' users interested in this job: %s' % recipients
      else:
        print ' no users interested in this job'
    if not recipients: continue  # no one is interested

    message = 'From: ' + fullname + ' <' + email + '>\n' +\
              'To: ' + string.join(recipients_with_fullnames,', ') + '\n' +\
              'Subject: PERFORCE job ' + jobname + ' for review\n' +\
              replyto_line +\
              '\n'
    for line in os.popen(p4 + ' job -o ' + jobname,'r').readlines():
      if line[0] != '#': message = message + line

    mailit(mailport, email, recipients, message)
    limit_emails = limit_emails - 1
    if limit_emails <= 0:
      complain( mailport, 'email limit exceeded in job review \
                           \n- extra jobs dropped!')
      break

  if debug and jobname and no_one_interested:
      print 'No users were interested in any of the jobs above - \
             perhaps no one has set the Reviews: field in their client\
             spec to include the "jobpath", namely "%s".  Please see "p4 \
             help user").' % jobpath
  set_counter(mailport,'jobreview',query_time)

def loop_body(mailhost):
  # Note: there's a try: wrapped around everything so that the program won't
  # halt.  Unfortunately, as a result you don't get the full traceback.
  # If you're debugging this script, remove the special exception handlers
  # to get the real traceback, or figure out how to get a real traceback,
  # by importing the traceback module and defining a file object that
  # will take the output of traceback.print_exc(file=mailfileobject)
  # and mail it (see the example in cgi.py)
  if debug: print 'Trying to open connection to SMTP (mail) \
                   server at host %s' % mailhost
  try:
    mailport=smtplib.SMTP(mailhost)
  except:
    sys.stderr.write('Unable to connect to SMTP host "' + mailhost \
                      + '"!\nWill try again in ' + repr(sleeptime) \
                      + ' seconds.\n')
  else:
    if debug: print 'SMTP connection open.'
    try:
      if notify_changes: review_changes(mailport,limit_emails)
      if notify_jobs: review_jobs(mailport,limit_emails)
    except:
      complain(mailport,'Review daemon problem:\n\n%s' % \
                  string.join(apply(traceback.format_exception,\
                  sys.exc_info()),''))
    try:
      mailport.quit()
    except:
      sys.stderr.write('Error while doing SMTP quit command (ignore).\n')


if __name__ == '__main__':
  if debug: print 'Entering main loop.'
  while(repeat):
    loop_body(mailhost)
    if debug: print 'Sleeping for %d seconds.' % sleeptime
    time.sleep(sleeptime)
  else:
    loop_body(mailhost)
  if debug: print 'Done.'
