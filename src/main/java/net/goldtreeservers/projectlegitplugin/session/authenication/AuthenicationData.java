package net.goldtreeservers.projectlegitplugin.session.authenication;

import java.net.InetAddress;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenicationData
{
	private UUID uniqueId;
	private InetAddress address;
}
