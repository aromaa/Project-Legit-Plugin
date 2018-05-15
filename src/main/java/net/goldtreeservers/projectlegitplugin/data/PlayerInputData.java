package net.goldtreeservers.projectlegitplugin.data;

import java.util.EnumSet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.projectlegitplugin.utils.EnumSetUtils;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PlayerInputData
{
	private final byte id;
	
	private EnumSet<PlayerInput> playerInput;
	private float yaw; //Change to short..?
	private float pitch; //Change to short..? Has smaller range space so it could be actualyl worth it

	public static PlayerInputData fromBytes(ByteBuf buf, PlayerInputData oldInput)
	{
		if (oldInput == null)
		{
			byte id = buf.readByte();
			short inputFlag = buf.readShort();
			float yaw = buf.readFloat();
			float pitch = buf.readFloat();
			
			return new PlayerInputData(id, EnumSetUtils.getEnumSet(PlayerInput.class, inputFlag), yaw, pitch);
		}
		else
		{
			byte stateFlag = buf.readByte();
			
			EnumSet<State> states = EnumSetUtils.getEnumSet(State.class, stateFlag);
			
			PlayerInputData input = new PlayerInputData((byte)(oldInput.getId() + 1));
			
			if (states.contains(State.INPUT_CHANGED))
			{
				short inputFlag = buf.readShort();
				
				input.playerInput = EnumSetUtils.getEnumSet(PlayerInput.class, inputFlag);
			}
			else
			{
				input.playerInput = oldInput.playerInput;
			}
			
			if (states.contains(State.YAW_CHANGED))
			{
				input.yaw = buf.readFloat();
			}
			else
			{
				input.yaw = oldInput.yaw;
			}
			
			if (states.contains(State.PITCH_CHANGED))
			{
				input.pitch = buf.readFloat();
			}
			else
			{
				input.pitch = oldInput.pitch;
			}
			
			return input;
		}
	}
	
	public int getInputFlag()
	{
		return EnumSetUtils.getFlag(this.playerInput);
	}
	
	private static enum State
	{
		INPUT_CHANGED,
		YAW_CHANGED,
		PITCH_CHANGED,
	}
}
