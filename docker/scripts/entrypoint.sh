#!/bin/bash

WMSAPP_HOME="$( readlink /usr/local/WowzaStreamingEngine )"

if [ -z $WSE_MGR_USER ]; then
	mgrUser="wowza"
else
	mgrUser=$WSE_MGR_USER
fi
if [ -z $WSE_MGR_PASS ]; then
	mgrPass="wowza"
else
	mgrPass=$WSE_MGR_PASS
fi

if [ ! -z $WSE_LIC ]; then
cat > ${WMSAPP_HOME}/conf/Server.license <<EOF
-----BEGIN LICENSE-----
${WSE_LIC}
-----END LICENSE-----
EOF
fi

echo -e "\n$mgrUser $mgrPass admin|advUser\n" >> ${WMSAPP_HOME}/conf/admin.password
echo -e "\n$mgrUser $mgrPass\n" >> ${WMSAPP_HOME}/conf/publish.password
echo -e "\n$mgrUser $mgrPass\n" >> ${WMSAPP_HOME}/conf/jmxremote.password
#echo -e "$mgrUser readwrite\n" >> ${WMSAPP_HOME}/conf/jmxremote.access

set -e
set -x


# Select default configuration for live application based on template profile
mv /usr/local/WowzaStreamingEngine/conf/live/Application.xml.live /usr/local/WowzaStreamingEngine/conf/live/Application.xml

# Select default template configuration based on template profile
mv /usr/local/WowzaStreamingEngine/conf/Application.xml.template /usr/local/WowzaStreamingEngine/conf/Application.xml

# Inject environment variables as configuration parameters where needed.
envs=`printenv`
for env in $envs
do
    IFS== read name value <<< "$env"

    [ -z "${name}" ] && echo "Warning: \${name} is not set"
    sed -i "s!\${${name}}!${value}!g" /usr/local/WowzaStreamingEngine/conf/live/Application.xml
    sed -i "s!\${${name}}!${value}!g" /usr/local/WowzaStreamingEngine/conf/Application.xml
done




if [[ ! -z $WSE_IP_PARAM ]]; then
	#change localhost to some user defined IP
	cat "${WMSAPP_HOME}/conf/Server.xml" > serverTmp
	sed 's|\(<IpAddress>localhost</IpAddress>\)|<IpAddress>'"$WSE_IP_PARAM"'</IpAddress> <!--changed for default install. \1-->|' <serverTmp >Server.xml
	sed 's|\(<RMIServerHostName>localhost</RMIServerHostName>\)|<RMIServerHostName>'"$WSE_IP_PARAM"'</RMIServerHostName> <!--changed for default install. \1-->|' <Server.xml >serverTmp
	cat serverTmp > ${WMSAPP_HOME}/conf/Server.xml
	rm serverTmp Server.xml

	cat "${WMSAPP_HOME}/conf/VHost.xml" > vhostTmp
	sed 's|\(<IpAddress>${com.wowza.wms.HostPort.IpAddress}</IpAddress>\)|<IpAddress>'"$WSE_IP_PARAM"'</IpAddress> <!--changed for default cloud install. \1-->|' <vhostTmp >${WMSAPP_HOME}/conf/VHost.xml
	rm vhostTmp
fi

# Make supervisor log files configurable
#sed 's|^logfile=.*|logfile='"${SUPERVISOR_LOG_HOME}"'/supervisor/supervisord.log ;|' -i /etc/supervisor/supervisord.conf

exec /usr/bin/supervisord -n -c /etc/supervisor/supervisord.conf